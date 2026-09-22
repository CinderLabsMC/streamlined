package net.streamlinedmod.streamlined.item;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.cable.CableType;
import net.streamlinedmod.streamlined.geo.renderer.CableItemRenderer;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public final class CableItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final CableType type;

    public CableItem(CableType type, Block block, Properties props) {
        super(block, props);

        this.type = type;
    }

    public CableType type() {
        return type;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {

            private GeoItemRenderer<?> renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer != null) {
                    return renderer;
                }

                renderer = new CableItemRenderer();

                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {}

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}