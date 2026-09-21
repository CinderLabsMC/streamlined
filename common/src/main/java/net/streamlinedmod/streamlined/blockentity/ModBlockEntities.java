package net.streamlinedmod.streamlined.blockentity;

import dev.architectury.registry.registries.DeferredRegister;
import net.streamlinedmod.streamlined.Streamlined;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Streamlined.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static void init() {
        BLOCK_ENTITIES.register();
    }
}
