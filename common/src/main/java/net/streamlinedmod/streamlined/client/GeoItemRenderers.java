package net.streamlinedmod.streamlined.client;

import com.geckolib.renderer.GeoItemRenderer;
import net.cinderlabsmc.cinderlib.block.geo.client.CinderGeoItemRenderer;
import net.cinderlabsmc.cinderlib.block.geo.client.CinderGeoModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.streamlinedmod.streamlined.Streamlined;
import net.streamlinedmod.streamlined.item.GeoModelItem;
import org.jspecify.annotations.NonNull;

public final class GeoItemRenderers {

    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "block/small_cable");

    private GeoItemRenderers() {}

    public static @NonNull GeoItemRenderer<GeoModelItem> create(@NonNull GeoModelItem item) {
        var id = BuiltInRegistries.ITEM.getKey(item);
        var model = id.withPrefix("item/");
        var texture = id.withPath("textures/item/" + id.getPath() + ".png");

        return new CinderGeoItemRenderer<>(new CinderGeoModel<>(model, texture, ANIMATION));
    }
}
