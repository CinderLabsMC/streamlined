package net.streamlinedmod.streamlined.item;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.storage.DriveContents;

public final class ModComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Streamlined.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<DriveContents>> DRIVE_CONTENTS = COMPONENTS.register("drive_contents",
            () -> DataComponentType.<DriveContents>builder()
                    .persistent(DriveContents.CODEC)
                    .networkSynchronized(DriveContents.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    public static final RegistrySupplier<DataComponentType<Long>> DISPLAY_COUNT = COMPONENTS.register("display_count",
            () -> DataComponentType.<Long>builder()
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
                    .build());

    public static final RegistrySupplier<DataComponentType<BlockState>> FACADE_BLOCK = COMPONENTS.register("facade_block",
            () -> DataComponentType.<BlockState>builder()
                    .persistent(BlockState.CODEC)
                    .networkSynchronized(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY))
                    .build());

    private ModComponents() {}

    public static void init() {
        COMPONENTS.register();
    }
}
