package bikerboys.wackylife;

import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import bikerboys.wackylife.attachements.*;
import bikerboys.wackylife.commands.*;
import bikerboys.wackylife.entity.*;
import static bikerboys.wackylife.entity.PlayerBoundEntity.isClient;
import bikerboys.wackylife.entity.triviabot.trivia.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.series.*;
import bikerboys.wackylife.util.*;
import bikerboys.wackylife.wyr.choice.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.fabricmc.fabric.api.resource.v1.*;
import net.minecraft.command.argument.serialize.*;
import net.fabricmc.fabric.api.command.v2.*;
import net.minecraft.resource.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import net.minecraft.world.tick.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WackyLife implements ModInitializer {

	public static MinecraftServer server;

	public static IClientHelper clientHelper;

	public static WackySeries wackyLife = new WackySeries();

	public static final String MOD_ID = "wackylife";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean isLogicalSide() {
		if (!isClient()) return true;
		return clientHelper != null && clientHelper.isRunningIntegratedServer();
	}

	@Override
	public void onInitialize() {
		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(QuestionResourceLoader.ID, new QuestionResourceLoader());
		ModAttachments.register();
		QuestionManager.registerQuestions();

		ServerLifecycleEvents.SERVER_STARTED.register(WackyLife::serverStarted);
		ServerLifecycleEvents.SERVER_STARTING.register(WackyLife::serverStarting);

		PayloadTypeRegistry.playS2C().register(AlivePlayerList.ID, AlivePlayerList.CODEC);
		PayloadTypeRegistry.playS2C().register(LivesAmountUpdate.ID, LivesAmountUpdate.CODEC);
		PayloadTypeRegistry.playS2C().register(CurrentSessionTime.ID, CurrentSessionTime.CODEC);
		PayloadTypeRegistry.playS2C().register(WackyPlayerMap.ID, WackyPlayerMap.CODEC);
		PayloadTypeRegistry.playS2C().register(WackySkinsActive.ID, WackySkinsActive.CODEC);
		PayloadTypeRegistry.playS2C().register(OpenChoicesScreen.ID, OpenChoicesScreen.CODEC);
		PayloadTypeRegistry.playS2C().register(DropItemS2C.ID, DropItemS2C.CODEC);
		PayloadTypeRegistry.playS2C().register(RandomSprintS2C.ID, RandomSprintS2C.CODEC);
		PayloadTypeRegistry.playS2C().register(S2COpenTriviaScreen.ID, S2COpenTriviaScreen.CODEC);
		PayloadTypeRegistry.playS2C().register(QuestionTimeLeftUpdateS2C.ID, QuestionTimeLeftUpdateS2C.CODEC);

		ModEntities.register();
		PayloadTypeRegistry.playC2S().register(SendChoices.ID, SendChoices.CODEC);
		PayloadTypeRegistry.playC2S().register(SubmitAnswerC2S.ID, SubmitAnswerC2S.SUBMIT_ANSWER_CODEC);

		ServerPlayNetworking.registerGlobalReceiver(SendChoices.ID, ((sendChoices, context) -> {
			Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();
			if (wildcardObj instanceof Choices choices) {
				choices.playerChose(context.player(), sendChoices.choices());
			}
		}));

		ServerPlayNetworking.registerGlobalReceiver(SubmitAnswerC2S.ID, ((payload, context) -> {

			if (WackyLife.wackyLife.getWildcardObj() instanceof TriviaWildcard wildcard) {
				TriviaWildcard.handleAnswer(context.player(), payload.index());
			}

		}));


		TaskScheduler.registerTickHandler();


		ChoiceRegistry.registerChoices();
		ChoiceManager.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			WackyCmdManager.register(dispatcher);
		});



		ArgumentTypeRegistry.registerArgumentType(
				Identifier.of(MOD_ID, "time"),
				WackyTimeArgumentType.class, ConstantArgumentSerializer.of(WackyTimeArgumentType::time));


		ScoreboardManager.INSTANCE.toString();

	}

	private static void serverStarting(MinecraftServer servers) {
		try {
			server = servers;
		} catch (Exception e) {

		}
	}

	private static void serverStarted(MinecraftServer servers) {
        try {
			server = servers;
        } catch (Exception e) {

		}
	}


	public static void setClientHelper(IClientHelper helper) {
		clientHelper = helper;
	}

}