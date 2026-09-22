package net.streamlinedmod.streamlined.storage;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.cable.CableType;
import net.streamlinedmod.streamlined.cable.part.PowerSupplyPart;
import net.streamlinedmod.streamlined.cable.part.StorageTerminalPart;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class StorageNetworks {

    private static final int REBUILD_TICKS = 20;
    private static final Direction[] DIRECTIONS = Direction.values();

    private static final Map<ServerLevel, LevelState> STATES = new WeakHashMap<>();

    private StorageNetworks() {}

    public static void init() {
        TickEvent.SERVER_LEVEL_POST.register(StorageNetworks::tick);
    }

    public static void add(@NonNull ServerLevel level, @NonNull Cable cable) {
        if (cable.type().type() != CableType.Type.STORAGE) {
            return;
        }

        var state = STATES.computeIfAbsent(level, _ -> new LevelState());

        state.cables.put(cable.getBlockPos().immutable(), cable);
        state.dirty = true;
    }

    public static void remove(@NonNull ServerLevel level, @NonNull Cable cable) {
        if (cable.type().type() != CableType.Type.STORAGE) {
            return;
        }

        var state = STATES.get(level);

        if (state != null && state.cables.remove(cable.getBlockPos(), cable)) {
            state.dirty = true;
        }
    }

    public static void markDirty(@NonNull Level level) {
        if (level instanceof ServerLevel server && STATES.get(server) instanceof LevelState state) {
            state.dirty = true;
        }
    }

    public static @Nullable StorageNetwork find(@NonNull ServerLevel level, @NonNull BlockPos cable) {
        var state = STATES.get(level);

        if (state == null) {
            return null;
        }

        if (state.dirty) {
            state.rebuild(level);
        }

        return state.byCable.get(cable);
    }

    private static void tick(ServerLevel level) {
        var state = STATES.get(level);

        if (state == null || state.cables.isEmpty()) {
            return;
        }

        if (state.dirty || level.getGameTime() % REBUILD_TICKS == 0 || state.networks.stream().anyMatch(StorageNetwork::isRemoved)) {
            state.rebuild(level);
        }

        for (var network : state.networks) {
            network.tick();
        }
    }

    private static final class LevelState {

        final Map<BlockPos, Cable> cables = new HashMap<>();
        final Map<BlockPos, StorageNetwork> byCable = new HashMap<>();

        List<StorageNetwork> networks = List.of();

        boolean dirty;

        void rebuild(ServerLevel level) {
            dirty = false;

            var previous = new HashMap<>(byCable);

            byCable.clear();

            var result = new ArrayList<StorageNetwork>();

            for (var start : cables.keySet()) {
                if (byCable.containsKey(start)) {
                    continue;
                }

                var members = new ArrayList<Cable>();
                var providers = new LinkedHashMap<BlockPos, StorageProvider>();
                var terminals = new ArrayList<StorageTerminalPart>();
                var supplies = new ArrayList<PowerSupplyPart>();
                var queue = new ArrayDeque<BlockPos>();
                var visited = new HashSet<BlockPos>();

                queue.add(start);
                visited.add(start);

                while (!queue.isEmpty()) {
                    var pos = queue.poll();
                    var cable = cables.get(pos);

                    if (cable == null || cable.isRemoved()) {
                        continue;
                    }

                    members.add(cable);

                    for (var part : cable.parts()) {
                        if (part instanceof StorageTerminalPart terminal) {
                            terminals.add(terminal);
                        } else if (part instanceof PowerSupplyPart supply) {
                            supplies.add(supply);
                        }
                    }

                    for (var dir : DIRECTIONS) {
                        if (!Cable.isConnected(level, pos, dir, CableType.Type.STORAGE)) {
                            continue;
                        }

                        var next = pos.relative(dir);

                        if (cables.containsKey(next)) {
                            if (visited.add(next)) {
                                queue.add(next);
                            }
                            continue;
                        }

                        if (level.getBlockEntity(next) instanceof StorageProvider provider) {
                            providers.putIfAbsent(next.immutable(), provider);
                        }
                    }
                }

                var network = new StorageNetwork(members, List.copyOf(providers.values()), terminals, supplies);

                if (previous.get(start) instanceof StorageNetwork old) {
                    network.inherit(old);
                }

                for (var cable : members) {
                    byCable.put(cable.getBlockPos(), network);
                }

                result.add(network);
            }

            networks = result;
        }
    }
}
