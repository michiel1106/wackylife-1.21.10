package bikerboys.wackylife;

import bikerboys.wackylife.commands.*;
import bikerboys.wackylife.event.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.series.*;
import bikerboys.wackylife.util.*;
import com.mojang.brigadier.tree.*;
import dev.architectury.event.events.common.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.command.argument.*;
import net.minecraft.command.argument.serialize.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.*;
import net.minecraft.server.command.*;
import static net.minecraft.server.command.CommandManager.*;
import net.fabricmc.fabric.api.command.v2.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WackyLife implements ModInitializer {

	public static WackySeries wackyLife = new WackySeries();

	public static final String MOD_ID = "wackylife";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(AlivePlayerList.ID, AlivePlayerList.CODEC);
		PayloadTypeRegistry.playS2C().register(LivesAmountUpdate.ID, LivesAmountUpdate.CODEC);


		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			WackyCmdManager.register(dispatcher);
		});



		ArgumentTypeRegistry.registerArgumentType(
				Identifier.of(MOD_ID, "time"),
				WackyTimeArgumentType.class, ConstantArgumentSerializer.of(WackyTimeArgumentType::time));


		ScoreboardManager.INSTANCE.toString();

	}
}