package bikerboys.wackylife.mixin.client.choices;

import bikerboys.wackylife.attachements.*;
import bikerboys.wackylife.gui.*;
import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.recipebook.*;
import net.minecraft.entity.player.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin({InventoryScreen.class})
public abstract class InventoryScreenMixin extends RecipeBookScreen<PlayerScreenHandler> {


    @Shadow public abstract void render(DrawContext context, int mouseX, int mouseY, float deltaTicks);

    public InventoryScreenMixin(PlayerScreenHandler handler, RecipeBookWidget<?> recipeBook, PlayerInventory inventory, Text title) {
        super(handler, recipeBook, inventory, title);
    }

    @Inject(
            method = {"init"},
            at = {@At("TAIL")}
    )
    private void wyr$addChoiceList(CallbackInfo ci) {
        ChoiceAttachment attached = client.player.getAttached(ModAttachments.CHOICE_ATTACHMENT);

        if (attached != null) {
            String negativeChoiceId = attached.negativeChoiceId();
            String positiveChoiceId = attached.positiveChoiceId();

            Choice negativeChoice = ChoiceRegistry.get(negativeChoiceId);
            Choice positiveChoice = ChoiceRegistry.get(positiveChoiceId);

            Collection<Choice> choiceInstances = client != null && client.player != null && attached != null ? List.of(positiveChoice, negativeChoice) : List.of();
            addDrawable(new ChoiceListWidget(0, 0, height, textRenderer, choiceInstances));
        }
    }
}