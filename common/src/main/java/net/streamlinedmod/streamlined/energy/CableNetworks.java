package net.streamlinedmod.streamlined.energy;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.streamlinedmod.streamlined.blockentity.CopperEnergyCableBlockEntity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class CableNetworks {

    private static final long UNLIMITED = Long.MAX_VALUE;

    private static final int ENDPOINT_REFRESH_TICKS = 10;
    private static final Direction[] DIRECTIONS = Direction.values();

    private static final Map<ServerLevel, LevelState> STATES = new WeakHashMap<>();

    private CableNetworks() {}

    public static void init() {
        TickEvent.SERVER_LEVEL_POST.register(CableNetworks::tick);
    }

    public static void add(ServerLevel level, CopperEnergyCableBlockEntity cable) {
        LevelState state = STATES.computeIfAbsent(level, l -> new LevelState());
        state.cables.put(cable.getBlockPos().immutable(), cable);
        state.dirty = true;
    }

    public static void remove(ServerLevel level, CopperEnergyCableBlockEntity cable) {
        LevelState state = STATES.get(level);
        if (state != null && state.cables.remove(cable.getBlockPos(), cable)) {
            state.dirty = true;
        }
    }

    private static void tick(ServerLevel level) {
        LevelState state = STATES.get(level);
        if (state == null || state.cables.isEmpty()) {
            return;
        }
        if (state.dirty) {
            state.rebuild();
        }
        long time = level.getGameTime();
        boolean refresh = time % ENDPOINT_REFRESH_TICKS == 0;
        for (Network network : state.networks) {
            network.tick(level, time, refresh);
        }
    }

    private static final class LevelState {
        final Map<BlockPos, CopperEnergyCableBlockEntity> cables = new HashMap<>();
        List<Network> networks = List.of();
        boolean dirty;

        void rebuild() {
            dirty = false;
            List<Network> result = new ArrayList<>();
            Set<BlockPos> visited = new HashSet<>();

            for (BlockPos start : cables.keySet()) {
                if (!visited.add(start)) {
                    continue;
                }
                List<CopperEnergyCableBlockEntity> members = new ArrayList<>();
                ArrayDeque<BlockPos> queue = new ArrayDeque<>();
                queue.add(start);
                while (!queue.isEmpty()) {
                    BlockPos pos = queue.poll();
                    members.add(cables.get(pos));
                    for (Direction dir : DIRECTIONS) {
                        BlockPos next = pos.relative(dir);
                        if (cables.containsKey(next) && visited.add(next)) {
                            queue.add(next);
                        }
                    }
                }
                result.add(new Network(members));
            }
            networks = result;
        }
    }

    private record Endpoint(BlockEntity be, EnergyHandle energy) {}

    private static final class Network {
        final List<CopperEnergyCableBlockEntity> cables;
        List<Endpoint> endpoints = List.of();
        boolean collected;
        final long flowRate;

        Network(List<CopperEnergyCableBlockEntity> cables) {
            this.cables = cables;
            this.flowRate = cables.stream().mapToLong(CopperEnergyCableBlockEntity::getFlowRate).min().orElse(0);
        }

        void collectEndpoints(ServerLevel level) {
            Map<BlockPos, Endpoint> unique = new HashMap<>();
            for (CopperEnergyCableBlockEntity cable : cables) {
                BlockPos pos = cable.getBlockPos();
                for (Direction dir : DIRECTIONS) {
                    BlockPos neighborPos = pos.relative(dir);
                    if (unique.containsKey(neighborPos)) {
                        continue;
                    }
                    BlockEntity be = level.getBlockEntity(neighborPos);
                    if (be == null || be instanceof CopperEnergyCableBlockEntity) {
                        continue;
                    }
                    EnergyHandle energy = be instanceof EnergyProvider provider
                            ? provider.getEnergy(dir.getOpposite())
                            : EnergyBridge.findExternal(level, neighborPos, dir.getOpposite());
                    if (energy != null) {
                        unique.put(neighborPos.immutable(), new Endpoint(be, energy));
                    }
                }
            }
            endpoints = new ArrayList<>(unique.values());
            collected = true;
        }

        void tick(ServerLevel level, long time, boolean refresh) {
            if (refresh || !collected) {
                collectEndpoints(level);
            }

            endpoints.removeIf(e -> e.be().isRemoved());
            if (endpoints.size() < 2) {
                return;
            }

            List<Endpoint> sources = new ArrayList<>();
            List<Endpoint> sinks = new ArrayList<>();
            List<Endpoint> buffers = new ArrayList<>();
            for (Endpoint endpoint : endpoints) {
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

            List<Endpoint> consumers = new ArrayList<>(sinks);
            consumers.addAll(buffers);

            long budget = flowRate;
            budget -= move(sources, consumers, time, budget);
            move(buffers, sinks, time, budget);
        }

        private static long move(List<Endpoint> from, List<Endpoint> to, long time, long limit) {
            if (from.isEmpty() || to.isEmpty() || limit <= 0) {
                return 0;
            }

            long supply = 0;
            long demand = 0;
            for (Endpoint e : from) supply += e.energy().extract(UNLIMITED, true);
            for (Endpoint e : to) demand += e.energy().insert(UNLIMITED, true);

            long total = Math.min(Math.min(supply, demand), limit);
            if (total <= 0) {
                return 0;
            }

            long remaining = total;
            for (int i = 0; i < from.size() && remaining > 0; i++) {
                Endpoint e = from.get((int) ((i + time) % from.size()));
                remaining -= e.energy().extract(remaining, false);
                e.be().setChanged();
            }

            remaining = total;
            for (int i = 0; i < to.size() && remaining > 0; i++) {
                Endpoint e = to.get((int) ((i + time) % to.size()));
                remaining -= e.energy().insert(remaining, false);
                e.be().setChanged();
            }
            return total;
        }
    }
}
