package bikerboys.wackylife.entity;

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

    public static final EntityType<TriviaBot> TRIVIA_BOT = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("wackylife", "trivia_bot"),
            EntityType.Builder.create(TriviaBot::new, SpawnGroup.CREATURE)
                    .maxTrackingRange(512)
                    .dimensions(0.65f, 1.8f) // Standard humanoid size
                    .build(TRIVIA_KEY) // The key here is technically optional in recent versions but good practice
    );

    public static void register() {
        // You MUST register attributes here, or the game will crash when the entity spawns.
        FabricDefaultAttributeRegistry.register(TRIVIA_BOT, TriviaBot.createAttributes());
    }
}