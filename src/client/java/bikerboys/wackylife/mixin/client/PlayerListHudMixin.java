package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import com.mojang.authlib.*;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
	@Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
	private void filterEntries(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
		if (WackyLifeClient.wackySkinsActive) {
			List<PlayerListEntry> original = cir.getReturnValue();
			if (original == null) return;
			if (WackyLifeClient.playerNameList.isEmpty()) return;

			List<PlayerListEntry> filtered = original.stream()
					.filter(e -> WackyLifeClient.playerNameList.contains(e.getProfile().name()))
					.map(e -> {
						GameProfile old = e.getProfile();
						Pair<String, Integer> stringIntegerPair = WackyLifeClient.playerNameMap.get(e.getProfile().name());


						GameProfile mapped = new GameProfile(old.id(), stringIntegerPair.getLeft());

						PlayerListEntry playerListEntry = new PlayerListEntry(mapped, true);

						Integer right = stringIntegerPair.getRight();

						Formatting color;
						if (right >= 4) {
							color = Formatting.DARK_GREEN;
						} else if (right == 3) {
							color = Formatting.GREEN;
						} else if (right == 2) {
							color = Formatting.YELLOW;
						} else if (right == 1) {
							color = Formatting.RED;
						} else {
							color = null;
						}

						Style style = Style.EMPTY;

                        if (e.getDisplayName() != null && e.getDisplayName().getStyle() != null) {
							style = e.getDisplayName().getStyle();
                        }

                        if (color != null) {
							style.withColor(color);
							playerListEntry.setDisplayName(Text.literal(stringIntegerPair.getLeft()).setStyle(style).formatted(color));
                        }

                        return playerListEntry;
					})
					.toList();


			cir.setReturnValue(filtered);
		}
	}
}