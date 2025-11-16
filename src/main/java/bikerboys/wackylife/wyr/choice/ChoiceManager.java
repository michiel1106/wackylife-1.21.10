package bikerboys.wackylife.wyr.choice;

import bikerboys.wackylife.attachements.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.world.*;

import java.util.*;

public class ChoiceManager {

    // --- REGISTRATION AND TICKING ---

    /**
     * Register this in your mod's main initializer to start the ticking.
     */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ChoiceManager::onServerTick);
    }

    /**
     * Called every server tick. Loops over all players.
     */
    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            tickPlayer(player);
        }
    }

    /**
     * Ticks a single player's choices.
     */
    private static void tickPlayer(ServerPlayerEntity player) {
        // 1. Get the player's saved data
        ChoiceAttachment attachment = ModAttachments.getChoice(player);

        // 2. Get the BEHAVIOR definitions for those choices
        Choice posChoiceDef = ChoiceRegistry.get(attachment.positiveChoiceId());
        Choice negChoiceDef = ChoiceRegistry.get(attachment.negativeChoiceId());

        // 3. Make *copies* of the data. This is important.
        NbtCompound posData = attachment.positiveData().copy();
        NbtCompound negData = attachment.negativeData().copy();

        boolean dataChanged = false;

        // 4. Tick the positive choice, giving it its data
        if (posChoiceDef != null) {
            posChoiceDef.tick(player.getEntityWorld(), player, posData);
            if (!posData.equals(attachment.positiveData())) {
                dataChanged = true;
            }
        }

        // 5. Tick the negative choice, giving it its data
        if (negChoiceDef != null) {
            negChoiceDef.tick(player.getEntityWorld(), player, negData);
            if (!negData.equals(attachment.negativeData())) {
                dataChanged = true;
            }
        }

        // 6. If data changed, save the new attachment
        if (dataChanged) {
            ChoiceAttachment newAttachment = new ChoiceAttachment(
                    attachment.positiveChoiceId(),
                    attachment.negativeChoiceId(),
                    posData,
                    negData
            );
            player.setAttached(ModAttachments.CHOICE_ATTACHMENT, newAttachment);
        }
    }

    // --- HELPER METHODS ---

    /**
     * Sets a player's choices, correctly handling onRemoval and onAddition logic.
     * Call this from your commands or other game logic.
     *
     * @param player            The player to modify.
     * @param newPositiveChoice The new positive choice definition.
     * @param newNegativeChoice The new negative choice definition.
     */
    public static void setChoice(ServerPlayerEntity player, Choice newPositiveChoice, Choice newNegativeChoice) {
        World world = player.getEntityWorld();

        // 1. Get the CURRENT (old) attachment
        ChoiceAttachment oldAttachment = ModAttachments.getChoice(player);
        Choice oldPositive = ChoiceRegistry.get(oldAttachment.positiveChoiceId());
        Choice oldNegative = ChoiceRegistry.get(oldAttachment.negativeChoiceId());

        // 2. Create the NEW attachment record.
        // The setChoices method creates new, empty NbtCompounds for data.
        ChoiceAttachment tempNewAttachment = oldAttachment.setChoices(
                newPositiveChoice.getId(),
                newNegativeChoice.getId()
        );

        // 3. Call onRemoval for old choices (if they existed)
        if (oldPositive != null) {
            oldPositive.onRemoval(world, player, oldAttachment.positiveData());
        }
        if (oldNegative != null) {
            oldNegative.onRemoval(world, player, oldAttachment.negativeData());
        }

        // 4. Get the new (and still empty) data tags
        NbtCompound newPosData = tempNewAttachment.positiveData();
        NbtCompound newNegData = tempNewAttachment.negativeData();

        // 5. Call onAddition for new choices. This will populate the new data tags.
        newPositiveChoice.onAddition(world, player, newPosData);
        newNegativeChoice.onAddition(world, player, newNegData);

        // 6. Create the *final* attachment with the initialized data
        ChoiceAttachment finalAttachment = new ChoiceAttachment(
                tempNewAttachment.positiveChoiceId(),
                tempNewAttachment.negativeChoiceId(),
                newPosData, // This tag now has default data from onAddition
                newNegData  // This tag now has default data from onAddition
        );

        // 7. Set the final attachment on the player
        player.setAttached(ModAttachments.CHOICE_ATTACHMENT, finalAttachment);
    }

    /**
     * Clears a player's choices, correctly handling onRemoval.
     *
     * @param player The player to modify.
     */
    public static void clearChoices(ServerPlayerEntity player) {
        World world = player.getEntityWorld();

        // 1. Get the CURRENT (old) attachment
        ChoiceAttachment oldAttachment = ModAttachments.getChoice(player);
        Choice oldPositive = ChoiceRegistry.get(oldAttachment.positiveChoiceId());
        Choice oldNegative = ChoiceRegistry.get(oldAttachment.negativeChoiceId());

        // 2. Call onRemoval for old choices (if they existed)
        if (oldPositive != null) {
            oldPositive.onRemoval(world, player, oldAttachment.positiveData());
        }
        if (oldNegative != null) {
            oldNegative.onRemoval(world, player, oldAttachment.negativeData());
        }

        // 3. Set the attachment to the DEFAULT (empty) state
        player.setAttached(ModAttachments.CHOICE_ATTACHMENT, ChoiceAttachment.DEFAULT);
    }
}