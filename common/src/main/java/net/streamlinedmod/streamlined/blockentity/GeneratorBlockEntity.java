package net.streamlinedmod.streamlined.blockentity;

import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.block.GeneratorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity implements EnergyProvider {
    private static final long PER_TICK = 20;

    private final SimpleEnergy energy = new SimpleEnergy(1_000, 0, 100);

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(GeneratorBlock.GENERATOR_BE.get(), pos, state);
    }

    @Override
    public @Nullable SimpleEnergy getEnergy(@Nullable Direction side) {
        return energy;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity gen) {
        // maxInsert=0 sperrt externes Einfügen, intern setzen wir direkt
        gen.energy.setAmount(gen.energy.getAmount() + PER_TICK);

        for (Direction dir : Direction.values()) {
            if (!(level.getBlockEntity(pos.relative(dir)) instanceof EnergyProvider provider)) continue;
            SimpleEnergy other = provider.getEnergy(dir.getOpposite());
            if (other == null) continue;
            long moved = other.insert(gen.energy.extract(Long.MAX_VALUE, true), true);
            if (moved > 0) other.insert(gen.energy.extract(moved, false), false);
        }
        gen.setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("energy", energy.getAmount());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy.setAmount(input.getLongOr("energy", 0));
    }
}
