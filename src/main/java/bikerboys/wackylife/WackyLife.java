package bikerboys.wackylife;

import bikerboys.wackylife.commands.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.series.*;
import bikerboys.wackylife.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.command.argument.serialize.*;
import net.fabricmc.fabric.api.command.v2.*;
import net.minecraft.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WackyLife implements ModInitializer {

	public static WackySeries wackyLife = new WackySeries();

	public static final String MOD_ID = "wackylife";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(AlivePlayerList.ID, AlivePlayerList.CODEC);
		PayloadTypeRegistry.playS2C().register(LivesAmountUpdate.ID, LivesAmountUpdate.CODEC);
		PayloadTypeRegistry.playS2C().register(CurrentSessionTime.ID, CurrentSessionTime.CODEC);
		PayloadTypeRegistry.playS2C().register(WackyPlayerMap.ID, WackyPlayerMap.CODEC);
		PayloadTypeRegistry.playS2C().register(WackySkinsActive.ID, WackySkinsActive.CODEC);


		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			WackyCmdManager.register(dispatcher);
		});



		ArgumentTypeRegistry.registerArgumentType(
				Identifier.of(MOD_ID, "time"),
				WackyTimeArgumentType.class, ConstantArgumentSerializer.of(WackyTimeArgumentType::time));


		ScoreboardManager.INSTANCE.toString();

	}
}