package bikerboys.wackylife;

import bikerboys.wackylife.networking.*;
import com.mojang.brigadier.arguments.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.loader.api.*;
import net.minecraft.server.command.*;

import java.util.*;

public class WackyLifeClient implements ClientModInitializer {
	public static List<String> playerNameList = new ArrayList<>();
	public static int currentlives = -1;

	public static float x = 0;
	public static float y = 0;
	public static float z = 0;

	public static float crosshairx = 0;
	public static float crosshairy = 0;
	public static float crosshairz = 0;


	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(AlivePlayerList.ID, ((alivePlayerList, context) -> {
			playerNameList = alivePlayerList.playerNames();
		}));

		ClientPlayNetworking.registerGlobalReceiver(LivesAmountUpdate.ID, ((livesAmountUpdate, context) -> {
			currentlives = livesAmountUpdate.integer();
		}));


		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			extracted();
		}

	}

	private static void extracted() {
		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register(ClientCommandManager.literal("setx").then(ClientCommandManager.argument("x", FloatArgumentType.floatArg()).executes(context -> {
			float x1 = FloatArgumentType.getFloat(context, "x");
			x = x1;
			return 0;
        }))));

		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register(ClientCommandManager.literal("sety").then(ClientCommandManager.argument("y", FloatArgumentType.floatArg()).executes(context -> {
			float y1 = FloatArgumentType.getFloat(context, "y");
			y = y1;
			return 0;
		}))));

		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register(ClientCommandManager.literal("setz").then(ClientCommandManager.argument("z", FloatArgumentType.floatArg()).executes(context -> {
			float z1 = FloatArgumentType.getFloat(context, "z");
			z = z1;
			return 0;
		}))));


		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register(ClientCommandManager.literal("setcrosshairx").then(ClientCommandManager.argument("x", FloatArgumentType.floatArg()).executes(context -> {
			float x1 = FloatArgumentType.getFloat(context, "x");
			crosshairx = x1;
			return 0;
		}))));

		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register(ClientCommandManager.literal("setcrosshairy").then(ClientCommandManager.argument("y", FloatArgumentType.floatArg()).executes(context -> {
			float y1 = FloatArgumentType.getFloat(context, "y");
			crosshairy = y1;
			return 0;
		}))));

		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register(ClientCommandManager.literal("setcrosshairz").then(ClientCommandManager.argument("z", FloatArgumentType.floatArg()).executes(context -> {
			float z1 = FloatArgumentType.getFloat(context, "z");
			crosshairz = z1;
			return 0;
		}))));
	}
}