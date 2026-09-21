package net.streamlinedmod.streamlined.blockentity;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.streamlinedmod.streamlined.energy.CableNetworks;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.block.CopperEnergyCableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CopperEnergyCableBlockEntity extends BlockEntity implements EnergyProvider, GeoBlockEntity {

    public static final long FLOW_RATE = 100;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CopperEnergyCableBlockEntity(BlockPos pos, BlockState state) {
        super(CopperEnergyCableBlock.CABLE_BE.get(), pos, state);
    }

    public long getFlowRate() {
        return FLOW_RATE;
    }

    @Override
    public @Nullable SimpleEnergy getEnergy(@Nullable Direction side) {
        return null;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (level instanceof ServerLevel serverLevel) {
            CableNetworks.add(serverLevel, this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level instanceof ServerLevel serverLevel) {
            CableNetworks.remove(serverLevel, this);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {

    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
