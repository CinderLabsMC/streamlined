package net.streamlinedmod.streamlined.storage;

import net.minecraft.network.chat.Component;
import net.streamlinedmod.streamlined.Streamlined;
import org.jspecify.annotations.NonNull;

public enum NetworkStatus {
    ONLINE,
    NO_POWER,
    NO_CHANNELS,
    OFFLINE;

    private static final NetworkStatus[] VALUES = values();

    public static @NonNull NetworkStatus byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : OFFLINE;
    }

    public @NonNull Component message() {
        return Component.translatable("gui." + Streamlined.MOD_ID + ".network." + name().toLowerCase());
    }
}
