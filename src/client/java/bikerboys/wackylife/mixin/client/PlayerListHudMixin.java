package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
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
		List<PlayerListEntry> original = cir.getReturnValue();
		if (original == null) return;
		if (WackyLifeClient.playerNameList.isEmpty()) return;

		List<PlayerListEntry> filtered = original.stream()
				.filter(e -> WackyLifeClient.playerNameList.contains(e.getProfile().name()))
				.toList();
		cir.setReturnValue(filtered);
	}
}