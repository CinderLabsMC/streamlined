package net.streamlinedmod.streamlined.compat.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.block.CableBlock;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Locale;

public enum StreamlinedJadeComponentProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendTooltip(@NonNull ITooltip tooltip, BlockAccessor accessor, @NonNull IPluginConfig config) {
        if (accessor.getBlock() instanceof CableBlock cable) {

            switch (cable.type().type()) {
                case ENERGY -> {
                    long rate = cable.type().rate();
                    String formattedRate = formattedRate(rate);

                    tooltip.add(Component.translatable("jade.streamlined.flow_rate", formattedRate));
                }
                case STORAGE -> {
                    long rate = cable.type().rate();

                    tooltip.add(Component.translatable("jade.streamlined.channels", rate));
                }
            }
            return;
        }

        int burn = accessor.getServerData().getIntOr(StreamlinedJadeDataProvider.BURN_KEY, 0);

        tooltip.add(burn > 0 ? Component.translatable("jade.streamlined.burning", burn / 20) : Component.translatable("jade.streamlined.idle"));
    }

    @Override
    public @NonNull Identifier getUid() {
        return Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "info");
    }

    private String formattedRate(long rate) {
        if (rate >= 1_000_000) {
            return String.format(Locale.US, "%.1fMFE/t", rate / 1_000_000.0);
        }
        if (rate >= 1_000) {
            return String.format(Locale.US, "%.1fkFE/t", rate / 1_000.0);
        }
        return rate + "FE/t";
    }
}
