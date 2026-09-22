package net.streamlinedmod.streamlined.blockentity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.cable.ModCables;
import net.streamlinedmod.streamlined.energy.EnergyBridge;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Streamlined.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<CableBlockEntity>> CABLE = BLOCK_ENTITIES.register("cable", () -> new BlockEntityType<>(CableBlockEntity::new, ModCables.blocks()));

    static {
        EnergyBridge.register(CABLE);
    }

    public static void init() {
        BLOCK_ENTITIES.register();
    }
}