package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import com.mojang.authlib.*;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.network.*;
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
						// Example: map all names to "bikerboys" but keep same UUID
						GameProfile mapped = new GameProfile(old.id(), "bikerboys");
						return new PlayerListEntry(mapped, true);
					})
					.toList();


			cir.setReturnValue(filtered);
		}
	}
}