package bikerboys.wackylife;

import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import bikerboys.wackylife.attachements.*;
import bikerboys.wackylife.commands.*;
import bikerboys.wackylife.entity.*;
import static bikerboys.wackylife.entity.PlayerBoundEntity.isClient;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.series.*;
import bikerboys.wackylife.util.*;
import bikerboys.wackylife.wyr.choice.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.command.argument.serialize.*;
import net.fabricmc.fabric.api.command.v2.*;
import net.minecraft.util.*;
import net.minecraft.world.tick.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WackyLife implements ModInitializer {

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
		ModAttachments.register();
		PayloadTypeRegistry.playS2C().register(AlivePlayerList.ID, AlivePlayerList.CODEC);
		PayloadTypeRegistry.playS2C().register(LivesAmountUpdate.ID, LivesAmountUpdate.CODEC);
		PayloadTypeRegistry.playS2C().register(CurrentSessionTime.ID, CurrentSessionTime.CODEC);
		PayloadTypeRegistry.playS2C().register(WackyPlayerMap.ID, WackyPlayerMap.CODEC);
		PayloadTypeRegistry.playS2C().register(WackySkinsActive.ID, WackySkinsActive.CODEC);
		PayloadTypeRegistry.playS2C().register(OpenChoicesScreen.ID, OpenChoicesScreen.CODEC);
		PayloadTypeRegistry.playS2C().register(DropItemS2C.ID, DropItemS2C.CODEC);
		PayloadTypeRegistry.playS2C().register(RandomSprintS2C.ID, RandomSprintS2C.CODEC);

		ModEntities.register();
		PayloadTypeRegistry.playC2S().register(SendChoices.ID, SendChoices.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(SendChoices.ID, ((sendChoices, context) -> {
			Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();
			if (wildcardObj instanceof Choices choices) {
				choices.playerChose(context.player(), sendChoices.choices());
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


	public static void setClientHelper(IClientHelper helper) {
		clientHelper = helper;
	}

}