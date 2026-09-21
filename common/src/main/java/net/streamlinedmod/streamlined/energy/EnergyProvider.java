package net.streamlinedmod.streamlined.energy;

import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public interface EnergyProvider {

    @Nullable SimpleEnergy getEnergy(@Nullable Direction side);

}
