package bikerboys.wackylife.attachements;

import bikerboys.wackylife.*;
import bikerboys.wackylife.wyr.choice.*;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

@SuppressWarnings("ALL")
public class ModAttachments {

    public static final AttachmentType<ChoiceAttachment> CHOICE_ATTACHMENT = AttachmentRegistry.create(
            Identifier.of(WackyLife.MOD_ID, "choice_pair"),
            builder -> builder
                    .initializer(() -> ChoiceAttachment.DEFAULT) // This handles "creates a new one"
                    .persistent(ChoiceAttachment.CODEC) // This handles "save stuff"
                    .copyOnDeath()
                    .syncWith(ChoiceAttachment.PACKET_CODEC, SyncPredicate.allIfNotDead())
    );

    /**
     * Gets the player's current choice attachment.
     * If the player doesn't have one, this CREATES and attaches the DEFAULT instance,
     * then returns it.
     */
    public static ChoiceAttachment getChoice(PlayerEntity player) {
        return player.getAttachedOrCreate(CHOICE_ATTACHMENT);
    }

    /**
     * Sets a player's choices, handling onRemoval and onAddition logic.
     * This should only be called on the server.
     *
     * @param player            The player to modify.
     * @param newPositiveChoice The new positive choice definition.
     * @param newNegativeChoice The new negative choice definition.
     */
    public static void setChoice(PlayerEntity player, Choice newPositiveChoice, Choice newNegativeChoice) {
        if (player.getEntityWorld().isClient()) {
            return;
        }

        World world = player.getEntityWorld();

        // 1. Get the CURRENT (old) attachment
        ChoiceAttachment oldAttachment = getChoice(player);
        Choice oldPositive = ChoiceRegistry.get(oldAttachment.positiveChoiceId());
        Choice oldNegative = ChoiceRegistry.get(oldAttachment.negativeChoiceId());

        // 2. Create the NEW attachment record.
        // The setChoices method creates new, empty CompoundTags for data.
        ChoiceAttachment tempNewAttachment = oldAttachment.setChoices(
                newPositiveChoice.getId(),
                newNegativeChoice.getId()
        );

        // 3. Call onRemoval for old choices (if they existed and are different)
        if (oldPositive != null && !oldPositive.getId().equals(newPositiveChoice.getId())) {
            oldPositive.onRemoval(world, player, oldAttachment.positiveData());
        }
        if (oldNegative != null && !oldNegative.getId().equals(newNegativeChoice.getId())) {
            oldNegative.onRemoval(world, player, oldAttachment.negativeData());
        }

        // 4. Get the new (and still empty) data tags
        NbtCompound newPosData = tempNewAttachment.positiveData();
        NbtCompound newNegData = tempNewAttachment.negativeData();

        // 5. Call onAddition for new choices. This will populate the new data tags.
        newPositiveChoice.onAddition(world, player, newPosData);
        newNegativeChoice.onAddition(world, player, newNegData);

        // 6. Create the *final* attachment with the initialized data
        // This is important because onAddition modified the tags.
        ChoiceAttachment finalAttachment = new ChoiceAttachment(
                tempNewAttachment.positiveChoiceId(),
                tempNewAttachment.negativeChoiceId(),
                newPosData, // This tag now has default data from onAddition
                newNegData  // This tag now has default data from onAddition
        );

        // 7. Set the final attachment on the player
        player.setAttached(CHOICE_ATTACHMENT, finalAttachment);
    }

    public static void setPositiveChoice(PlayerEntity player, Choice newPositiveChoice) {
        if (player.getEntityWorld().isClient()) {
            return;
        }

        World world = player.getEntityWorld();

        ChoiceAttachment oldAttachment = getChoice(player);
        Choice oldPositive = ChoiceRegistry.get(oldAttachment.positiveChoiceId());

        ChoiceAttachment tempNewAttachment = oldAttachment.setChoices(
                newPositiveChoice.getId(),
                oldAttachment.negativeChoiceId()
        );

        if (oldPositive != null && !oldPositive.getId().equals(newPositiveChoice.getId())) {
            oldPositive.onRemoval(world, player, oldAttachment.positiveData());
        }

        NbtCompound newPosData = tempNewAttachment.positiveData();
        NbtCompound negData = tempNewAttachment.negativeData();

        newPositiveChoice.onAddition(world, player, newPosData);

        ChoiceAttachment finalAttachment = new ChoiceAttachment(
                tempNewAttachment.positiveChoiceId(),
                tempNewAttachment.negativeChoiceId(),
                newPosData,
                negData
        );

        player.setAttached(CHOICE_ATTACHMENT, finalAttachment);
    }


    public static void setNegativeChoice(PlayerEntity player, Choice newNegativeChoice) {
        if (player.getEntityWorld().isClient()) {
            return;
        }

        World world = player.getEntityWorld();

        ChoiceAttachment oldAttachment = getChoice(player);
        Choice oldNegative = ChoiceRegistry.get(oldAttachment.negativeChoiceId());

        ChoiceAttachment tempNewAttachment = oldAttachment.setChoices(
                oldAttachment.positiveChoiceId(),
                newNegativeChoice.getId()
        );

        if (oldNegative != null && !oldNegative.getId().equals(newNegativeChoice.getId())) {
            oldNegative.onRemoval(world, player, oldAttachment.negativeData());
        }

        NbtCompound posData = tempNewAttachment.positiveData();
        NbtCompound newNegData = tempNewAttachment.negativeData();

        newNegativeChoice.onAddition(world, player, newNegData);

        ChoiceAttachment finalAttachment = new ChoiceAttachment(
                tempNewAttachment.positiveChoiceId(),
                tempNewAttachment.negativeChoiceId(),
                posData,
                newNegData
        );

        player.setAttached(CHOICE_ATTACHMENT, finalAttachment);
    }


    /**
     * Call this from your mod's main initializer to ensure the
     * static CHOICE_ATTACHMENT field is initialized and registered.
     */
    static {
        WackyLife.LOGGER.info("ModAttachments class loaded, CHOICE_ATTACHMENT initialized");
    }

    public static void register() {
        WackyLife.LOGGER.info("register() called, CHOICE_ATTACHMENT = {}", CHOICE_ATTACHMENT);
    }
}