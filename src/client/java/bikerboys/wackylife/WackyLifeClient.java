package bikerboys.wackylife;

import bikerboys.wackylife.attachements.*;
import bikerboys.wackylife.choices.*;
import bikerboys.wackylife.gui.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.wyr.choice.*;
import com.mojang.authlib.minecraft.client.*;
import com.mojang.brigadier.arguments.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.keybinding.v1.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.fabricmc.fabric.impl.screenhandler.client.*;
import net.fabricmc.loader.api.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.*;
import net.minecraft.client.option.*;
import net.minecraft.client.util.*;
import net.minecraft.server.command.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.lwjgl.glfw.*;

import java.util.*;

public class WackyLifeClient implements ClientModInitializer {
	public static List<String> playerNameList = new ArrayList<>();
	public static Map<String, String> playerNameMap = new HashMap<>();
	private static final ClientTicker CLIENT_TICKER;


	public static boolean wackySkinsActive = false;
	public static int currentlives = -1;
	public static int currentsessiontime = -1;

	public static float x = 0;
	public static float y = 0;
	public static float z = 0;

	public static float crosshairx = 0;
	public static float crosshairy = 0;
	public static float crosshairz = 0;

	static {
		CLIENT_TICKER = new ClientTicker();
	}

	public static int getTick() {
		return CLIENT_TICKER.tick;
	}

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(CLIENT_TICKER);
		ClientTickEvents.END_WORLD_TICK.register(ChoiceTicker::tick);

		ClientPlayNetworking.registerGlobalReceiver(OpenChoicesScreen.ID, ((openChoicesScreen, context) -> {
			context.client().setScreen(new ChoicesScreen(openChoicesScreen.choices().getFirst(), openChoicesScreen.choices().getLast()));
		}));

		ClientPlayNetworking.registerGlobalReceiver(AlivePlayerList.ID, ((alivePlayerList, context) -> {
			WackyLifeClient.playerNameList = alivePlayerList.playerNames();
		}));

		ClientPlayNetworking.registerGlobalReceiver(WackySkinsActive.ID, ((wackySkinsActive1, context) -> {
			wackySkinsActive = wackySkinsActive1.active();
		}));

		ClientPlayNetworking.registerGlobalReceiver(DropItemS2C.ID, ((dropItemS2C, context) -> {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.dropSelectedItem(true);
            }
        }));

		ClientPlayNetworking.registerGlobalReceiver(WackyPlayerMap.ID, ((WackyPlayerMap, context) -> {
			WackyLifeClient.playerNameMap.clear();
			WackyLifeClient.playerNameMap.putAll(WackyPlayerMap.players());
		}));

		ClientPlayNetworking.registerGlobalReceiver(LivesAmountUpdate.ID, ((livesAmountUpdate, context) -> {
			WackyLifeClient.currentlives = livesAmountUpdate.integer();
		}));

		ClientPlayNetworking.registerGlobalReceiver(RandomSprintS2C.ID, ((randomSprintS2C, context) -> {
			boolean sprint = randomSprintS2C.sprint();
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.setSprinting(!sprint);
            }
        }));

		ClientPlayNetworking.registerGlobalReceiver(CurrentSessionTime.ID, ((currentSessionTime, context) -> {
			WackyLifeClient.currentsessiontime = currentSessionTime.integer();
		}));

		HudRenderCallback.EVENT.register(((drawContext, tickDelta) -> {
			if (currentsessiontime < 0) currentsessiontime = 0;

			int totalSeconds = currentsessiontime / 20;
			int hours = totalSeconds / 3600;
			int minutes = (totalSeconds % 3600) / 60;
			int seconds = totalSeconds % 60;

			String text = String.format("%d:%02d:%02d", hours, minutes, seconds);

			float scale = 1.1f; // 1.0 = normal, 2.0 = double size
			var matrices = drawContext.getMatrices();
			matrices.pushMatrix();

			matrices.scale(scale, scale);
			MinecraftClient client = MinecraftClient.getInstance();
			int sw = client.getWindow().getScaledWidth();
			int sh = client.getWindow().getScaledHeight();
			int textWidth = client.textRenderer.getWidth(text);

			// adjust for scaling
			float x = (sw / scale) - textWidth - 4;
			float y = (sh / scale) - 10;

			drawContext.drawText(client.textRenderer, text, (int)x, (int)y, 0xFFAAAAAA, false);

			matrices.popMatrix();
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





	static class ClientTicker implements ClientTickEvents.EndTick {
		private int tick;
		private boolean hadRandomShaders;
		private boolean hadUnsupportedTransparency;

		ClientTicker() {
		}

		public void onEndTick(MinecraftClient client) {
            if (client.player != null) {
				boolean b = ModAttachments.getChoice(client.player).negativeChoiceId().equalsIgnoreCase("hotbarfov");

				if (b) {
					switch (client.player.getInventory().getSelectedSlot()) {
						case 0 -> client.options.getFov().setValue(30);
						case 1 -> client.options.getFov().setValue(110);
						case 2 -> client.options.getFov().setValue(40);
						case 3 -> client.options.getFov().setValue(60);
						case 4 -> client.options.getFov().setValue(50);
						case 5 -> client.options.getFov().setValue(80);
						case 6 -> client.options.getFov().setValue(70);
						case 7 -> client.options.getFov().setValue(100);
						case 8 -> client.options.getFov().setValue(90);

					}

				}

			}
                ++this.tick;
		}
	}
}