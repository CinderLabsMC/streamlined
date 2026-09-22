package net.streamlinedmod.streamlined.compat.jade;

import net.cinderlabsmc.cinderlib.block.CinderEntityBlock;
import net.streamlinedmod.streamlined.block.Cable;
import net.streamlinedmod.streamlined.block.Generator;
import net.streamlinedmod.streamlined.cable.part.Attachment;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class StreamlinedJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(StreamlinedJadeDataProvider.INSTANCE, Generator.class);
        registration.registerBlockDataProvider(StreamlinedJadeDataProvider.INSTANCE, Cable.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(StreamlinedJadeComponentProvider.INSTANCE, CinderEntityBlock.class);

        registration.addRayTraceCallback((hit, accessor, original) -> {
            if (accessor instanceof BlockAccessor block && block.getBlockEntity() instanceof Cable cable && cable.attachmentAt(block.getHitResult()) instanceof Attachment attachment) {
                return registration.blockAccessor().from(block).serversideRep(cable.attachmentItem(attachment)).build();
            }

            return accessor;
        });
    }
}