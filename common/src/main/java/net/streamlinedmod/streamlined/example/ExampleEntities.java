package net.streamlinedmod.streamlined.example;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.streamlinedmod.streamlined.Streamlined;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** Beispiel: Entity-Typ registrieren + Attribute. */
public final class ExampleEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Streamlined.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<ExampleEntity>> EXAMPLE_ENTITY = ENTITIES.register("example_entity", () ->
            EntityType.Builder.of(ExampleEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 0.8f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Streamlined.MOD_ID, "example_entity"))));

    private ExampleEntities() {}

    public static void init() {
        ENTITIES.register();
        EntityAttributeRegistry.register(EXAMPLE_ENTITY, ExampleEntity::createAttributes);
    }
}
