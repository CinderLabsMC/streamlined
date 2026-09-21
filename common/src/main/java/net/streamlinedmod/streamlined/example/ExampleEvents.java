package net.streamlinedmod.streamlined.example;

import dev.architectury.event.events.common.PlayerEvent;
import net.streamlinedmod.streamlined.Streamlined;

public final class ExampleEvents {

    private ExampleEvents() {}

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(player ->
                Streamlined.LOGGER.info("{} ist beigetreten", player.getName().getString()));
    }
}
