package net.streamlinedmod.streamlined.item;

import org.jspecify.annotations.NonNull;

import java.util.Locale;

public enum DriveState {
    EMPTY,
    USED,
    TYPES_FULL,
    FULL;

    public @NonNull String id() {
        return name().toLowerCase(Locale.ROOT);
    }
}
