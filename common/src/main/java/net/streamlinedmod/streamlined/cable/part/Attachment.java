package net.streamlinedmod.streamlined.cable.part;

import net.minecraft.core.Direction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record Attachment(@NonNull Direction side, @Nullable CablePart part) {

    public static @NonNull Attachment facade(@NonNull Direction side) {
        return new Attachment(side, null);
    }

    public boolean isFacade() {
        return part == null;
    }
}
