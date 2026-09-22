package net.streamlinedmod.streamlined.cable.part;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.streamlinedmod.streamlined.block.Cable;

public final class CablePartEvents {

    private CablePartEvents() {}

    public static void init() {
        BlockEvent.BREAK.register((level, pos, state, player) -> {
            if (!(level.getBlockEntity(pos) instanceof Cable cable)) {
                return EventResult.pass();
            }

            var attachment = cable.targetAttachment(player);

            if (attachment == null) {
                return EventResult.pass();
            }

            cable.breakAttachment(attachment, player);

            return EventResult.interruptFalse();
        });
    }
}
