package net.streamlinedmod.streamlined.compat.jade;

import net.cinderlabsmc.cinderlib.block.CinderEntityBlock;
import net.streamlinedmod.streamlined.block.Generator;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class StreamlinedJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(StreamlinedJadeDataProvider.INSTANCE, Generator.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(StreamlinedJadeComponentProvider.INSTANCE, CinderEntityBlock.class);
    }
}