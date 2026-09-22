package net.streamlinedmod.streamlined.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.Generator;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum StreamlinedJadeDataProvider implements IServerDataProvider<BlockAccessor> {

    INSTANCE;

    static final String BURN_KEY = "streamlined_burn";

    @Override
    public void appendServerData(@NonNull CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof Generator generator) {
            data.putInt(BURN_KEY, generator.getBurnTime());
        }
    }

    @Override
    public @NonNull Identifier getUid() {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "info_data");
    }
}
