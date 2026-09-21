package net.streamlinedmod.streamlined.compat.jade;

import net.streamlinedmod.streamlined.block.CopperEnergyCableBlock;
import net.streamlinedmod.streamlined.block.GeneratorBlock;
import net.streamlinedmod.streamlined.blockentity.GeneratorBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class StreamlinedJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(StreamlinedJadeDataProvider.INSTANCE, GeneratorBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(StreamlinedJadeComponentProvider.INSTANCE, GeneratorBlock.class);
        registration.registerBlockComponent(StreamlinedJadeComponentProvider.INSTANCE, CopperEnergyCableBlock.class);
    }
}
