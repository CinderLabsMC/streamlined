package net.streamlinedmod.streamlined.item;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.streamlinedmod.streamlined.geo.renderer.CopperEnergyCableItemRenderer;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class CopperEnergyCableItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CopperEnergyCableItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<?> renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new CopperEnergyCableItemRenderer();
                }
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
