package net.streamlinedmod.streamlined.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.block.Generator;
import net.streamlinedmod.streamlined.cable.part.Attachment;
import net.streamlinedmod.streamlined.cable.part.PowerSupplyPart;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum StreamlinedJadeDataProvider implements IServerDataProvider<BlockAccessor> {

    INSTANCE;

    static final String BURN_KEY = "streamlined_burn";
    static final String ENERGY_KEY = "streamlined_energy";
    static final String CAPACITY_KEY = "streamlined_capacity";

    @Override
    public void appendServerData(@NonNull CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof Generator generator) {
            data.putInt(BURN_KEY, generator.getBurnTime());
        }

        if (accessor.getBlockEntity() instanceof Cable cable && cable.attachmentAt(accessor.getHitResult()) instanceof Attachment attachment && attachment.part() instanceof PowerSupplyPart supply) {
            data.putLong(ENERGY_KEY, supply.energy().getAmount());
            data.putLong(CAPACITY_KEY, supply.energy().getCapacity());
        }
    }

    @Override
    public @NonNull Identifier getUid() {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "info_data");
    }
}
