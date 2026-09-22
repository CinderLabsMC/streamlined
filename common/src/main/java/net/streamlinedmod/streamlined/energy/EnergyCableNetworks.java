package net.streamlinedmod.streamlined.energy;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.cable.CableType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class EnergyCableNetworks {

    public static final long UNLIMITED = Long.MAX_VALUE;

    private static final int ENDPOINT_REFRESH_TICKS = 10;
    private static final Direction[] DIRECTIONS = Direction.values();

    private static final Map<ServerLevel, LevelState> STATES = new WeakHashMap<>();

    private EnergyCableNetworks() {}

    public static void init() {
        TickEvent.SERVER_LEVEL_POST.register(EnergyCableNetworks::tick);
    }

    public static void add(ServerLevel level, Cable cable) {
        if (cable.type().type() != CableType.Type.ENERGY) {
            return;
        }

        var state = STATES.computeIfAbsent(level, _ -> new LevelState());

        state.cables.put(cable.getBlockPos().immutable(), cable);
        state.dirty = true;
    }

    public static void remove(ServerLevel level, Cable cable) {
        if (cable.type().type() != CableType.Type.ENERGY) {
            return;
        }

        var state = STATES.get(level);

        if (state == null) {
            return;
        }

        if (!state.cables.remove(cable.getBlockPos(), cable)) {
            return;
        }

        state.dirty = true;
    }

    public static void markDirty(Level level) {
        if (level instanceof ServerLevel server && STATES.get(server) instanceof LevelState state) {
            state.dirty = true;
        }
    }

    private static void tick(ServerLevel level) {
        var state = STATES.get(level);

        if (state == null || state.cables.isEmpty()) {
            return;
        }

        if (state.dirty) {
            state.rebuild();
        }

        long time = level.getGameTime();
        boolean refresh = time % ENDPOINT_REFRESH_TICKS == 0;

        for (var network : state.networks) {
            network.tick(level, time, refresh);
        }
    }

    private static final class LevelState {

        final Map<BlockPos, Cable> cables = new HashMap<>();

        List<Network> networks = List.of();

        boolean dirty;

        void rebuild() {
            dirty = false;

            var result = new ArrayList<Network>();
            var visited = new HashSet<BlockPos>();

            for (var start : cables.keySet()) {
                if (!visited.add(start)) {
                    continue;
                }

                var members = new ArrayList<Cable>();
                var queue = new ArrayDeque<BlockPos>();

                queue.add(start);

                while (!queue.isEmpty()) {
                    var pos = queue.poll();
                    var cable = cables.get(pos);

                    if (cable == null) {
                        continue;
                    }

                    members.add(cable);

                    for (var dir : DIRECTIONS) {
                        var next = pos.relative(dir);

                        if (!cables.containsKey(next) || !Cable.isConnected(cable.getLevel(), pos, dir, CableType.Type.ENERGY)) {
                            continue;
                        }

                        if (!visited.add(next)) {
                            continue;
                        }

                        queue.add(next);
                    }
                }

                result.add(new Network(members));
            }

            networks = result;
        }
    }

    private record Endpoint(BlockEntity be, EnergyHandle energy) {}

    private static final class Network {

        final List<Cable> cables;
        final long rate;

        List<Endpoint> endpoints = List.of();

        boolean collected;

        Network(List<Cable> cables) {
            this.cables = cables;
            this.rate = cables.stream().mapToLong(Cable::rate).min().orElse(0);
        }

        void collectEndpoints(ServerLevel level) {
            var unique = new HashMap<BlockPos, Endpoint>();

            for (var cable : cables) {
                var pos = cable.getBlockPos();

                for (var dir : DIRECTIONS) {
                    var neighborPos = pos.relative(dir);

                    if (unique.containsKey(neighborPos) || cable.isBlocked(dir)) {
                        continue;
                    }

                    var be = level.getBlockEntity(neighborPos);

                    if (be == null) {
                        continue;
                    }

                    if (be instanceof Cable other && other.type().type() == CableType.Type.ENERGY) {
                        continue;
                    }

                    EnergyHandle energy;

                    if (be instanceof EnergyProvider provider) {
                        energy = provider.getEnergy(dir.getOpposite());
                    } else {
                        energy = EnergyBridge.findExternal(level, neighborPos, dir.getOpposite());
                    }

                    if (energy == null) {
                        continue;
                    }

                    unique.put(neighborPos.immutable(), new Endpoint(be, energy));
                }
            }

            endpoints = new ArrayList<>(unique.values());
            collected = true;
        }

        void tick(ServerLevel level, long time, boolean refresh) {
            if (refresh || !collected) {
                collectEndpoints(level);
            }

            endpoints.removeIf(endpoint -> endpoint.be().isRemoved());

            if (endpoints.size() < 2) {
                return;
            }

            var sources = new ArrayList<Endpoint>();
            var sinks = new ArrayList<Endpoint>();
            var buffers = new ArrayList<Endpoint>();

            for (var endpoint : endpoints) {
                boolean out = endpoint.energy().extract(UNLIMITED, true) > 0;
                boolean in = endpoint.energy().insert(UNLIMITED, true) > 0;

                if (out && !in) {
                    sources.add(endpoint);
                    continue;
                }

                if (in && !out) {
                    sinks.add(endpoint);
                    continue;
                }

                if (in) {
                    buffers.add(endpoint);
                }
            }

            var consumers = new ArrayList<>(sinks);

            consumers.addAll(buffers);

            long budget = rate;

            budget -= move(sources, consumers, time, budget);

            move(buffers, sinks, time, budget);
        }

        private static long move(List<Endpoint> from, List<Endpoint> to, long time, long limit) {
            if (from.isEmpty() || to.isEmpty() || limit <= 0) {
                return 0;
            }

            long supply = 0;
            long demand = 0;

            for (var endpoint : from) {
                supply += endpoint.energy().extract(UNLIMITED, true);
            }

            for (var endpoint : to) {
                demand += endpoint.energy().insert(UNLIMITED, true);
            }

            long total = Math.min(Math.min(supply, demand), limit);

            if (total <= 0) {
                return 0;
            }

            long remaining = total;

            for (int i = 0; i < from.size() && remaining > 0; i++) {
                var endpoint = from.get((int) ((i + time) % from.size()));

                remaining -= endpoint.energy().extract(remaining, false);
                endpoint.be().setChanged();
            }

            remaining = total;

            for (int i = 0; i < to.size() && remaining > 0; i++) {
                var endpoint = to.get((int) ((i + time) % to.size()));

                remaining -= endpoint.energy().insert(remaining, false);
                endpoint.be().setChanged();
            }

            return total;
        }
    }
}