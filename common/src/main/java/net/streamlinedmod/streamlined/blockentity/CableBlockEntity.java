package net.streamlinedmod.streamlined.blockentity;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.streamlinedmod.streamlined.block.CableBlock;
import net.streamlinedmod.streamlined.cable.CableType;
import net.streamlinedmod.streamlined.energy.EnergyCableNetworks;
import net.streamlinedmod.streamlined.energy.EnergyProvider;
import net.streamlinedmod.streamlined.energy.SimpleEnergy;
import net.streamlinedmod.streamlined.storage.StorageProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class CableBlockEntity extends BlockEntity implements EnergyProvider, StorageProvider, GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE.get(), pos, state);
    }

    public CableType type() {
        return ((CableBlock) getBlockState().getBlock()).type();
    }

    public long rate() {
        return type().rate();
    }

    @Override
    public @Nullable SimpleEnergy getEnergy(@Nullable Direction side) {
        return null;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (!(level instanceof ServerLevel server)) {
            return;
        }

        EnergyCableNetworks.add(server, this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (!(level instanceof ServerLevel server)) {
            return;
        }

        EnergyCableNetworks.remove(server, this);
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {}

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}