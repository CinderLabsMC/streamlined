package net.streamlinedmod.streamlined.geo;

import com.geckolib.constant.dataticket.DataTicket;
import net.minecraft.resources.Identifier;

public final class CableRenderData {

    public static final DataTicket<Integer> CONNECTIONS = DataTicket.create("streamlined_cable_connections", Integer.class);
    public static final DataTicket<Identifier> MODEL = DataTicket.create("streamlined_cable_model", Identifier.class);
    public static final DataTicket<Identifier> TEXTURE = DataTicket.create("streamlined_cable_texture", Identifier.class);
    public static final DataTicket<Float> SCALE = DataTicket.create("streamlined_cable_scale", Float.class);

    private CableRenderData() {}
}