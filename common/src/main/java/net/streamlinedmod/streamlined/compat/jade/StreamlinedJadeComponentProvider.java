package net.streamlinedmod.streamlined.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.CopperEnergyCableBlock;
import net.streamlinedmod.streamlined.blockentity.CopperEnergyCableBlockEntity;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum StreamlinedJadeComponentProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendTooltip(@NonNull ITooltip tooltip, BlockAccessor accessor, @NonNull IPluginConfig config) {
        if (accessor.getBlock() instanceof CopperEnergyCableBlock) {
            tooltip.add(Component.translatable("jade.streamlined.flow_rate", CopperEnergyCableBlockEntity.FLOW_RATE));
            return;
        }

        int burn = accessor.getServerData().getIntOr(StreamlinedJadeDataProvider.BURN_KEY, 0);
        tooltip.add(burn > 0
                ? Component.translatable("jade.streamlined.burning", burn / 20)
                : Component.translatable("jade.streamlined.idle"));
    }

    @Override
    public @NonNull Identifier getUid() {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "info");
    }
}
