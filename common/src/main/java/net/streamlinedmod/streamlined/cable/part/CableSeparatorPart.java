package net.streamlinedmod.streamlined.cable.part;

import net.minecraft.core.Direction;
import net.streamlinedmod.streamlined.block.Cable;
import org.jspecify.annotations.NonNull;

public final class CableSeparatorPart extends CablePart {

    public CableSeparatorPart(@NonNull Cable host, @NonNull Direction side) {
        super(PartType.CABLE_SEPARATOR, host, side);
    }
}
