package bikerboys.wackylife.entity;

import bikerboys.wackylife.entity.snail.*;
import bikerboys.wackylife.entity.triviabot.TriviaBot;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final RegistryKey<EntityType<?>> TRIVIA_KEY = RegistryKey.of(
            Registries.ENTITY_TYPE.getKey(),
            TriviaBot.ID
    );

    public static final RegistryKey<EntityType<?>> SNAIL_KEY = RegistryKey.of(
            Registries.ENTITY_TYPE.getKey(),
            Snail.ID
    );

    public static final EntityType<TriviaBot> TRIVIA_BOT = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("wackylife", "trivia_bot"),
            EntityType.Builder.create(TriviaBot::new, SpawnGroup.CREATURE)
                    .maxTrackingRange(512)
                    .dimensions(0.65f, 1.8f) // Standard humanoid size
                    .build(TRIVIA_KEY)
    );

    public static final EntityType<Snail> SNAIL = Registry.register(
            Registries.ENTITY_TYPE,
            Snail.ID,
            EntityType.Builder.create(Snail::new, SpawnGroup.MONSTER)
                    .dimensions(0.5f, 0.6f)
                    .maxTrackingRange(512)
                    .build(SNAIL_KEY)
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(TRIVIA_BOT, TriviaBot.createAttributes());
        FabricDefaultAttributeRegistry.register(SNAIL, Snail.createAttributes());
    }
}