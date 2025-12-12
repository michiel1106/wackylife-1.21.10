package bikerboys.wackylife.wyr.choice;

import bikerboys.wackylife.attachements.*;
import dev.architectury.event.*;
import dev.architectury.event.events.common.*;
import dev.architectury.event.events.common.BlockEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.tag.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ChoiceManager {
    private static final List<ScheduledGrow> growTasks = new ArrayList<>();
    /**
     * Register this in your mod's main initializer to start the ticking.
     */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ChoiceManager::onServerTick);
        BlockEvent.PLACE.register(ChoiceManager::onPlaceBlock);
        EntityEvent.LIVING_HURT.register(ChoiceManager::onDamage);
        


    }

    private static EventResult onDamage(LivingEntity entity, DamageSource damageSource, float v) {

        if (!entity.getEntityWorld().isClient()) {
            if (entity instanceof PlayerEntity player) {
                if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("no_fire_damage")) {
                    if (damageSource.isOf(DamageTypes.ON_FIRE) || damageSource.isIn(DamageTypeTags.IS_FIRE)) {
                        return EventResult.interruptFalse();
                    }


                }

            }
        }

        return EventResult.pass();
    }

    private static EventResult onPlaceBlock(World world, BlockPos pos, BlockState state, @Nullable Entity entity) {
        if (!world.isClient()) {
            if (entity instanceof PlayerEntity player) {
                if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("instant_bonemeal")) {
                    if (world instanceof ServerWorld serverWorld) {
                        growTasks.add(new ScheduledGrow(serverWorld, pos, 1));
                    }
                }
            }
        }


        if (entity != null) {
            if (entity instanceof PlayerEntity player) {
                if (ModAttachments.getChoice(player).negativeChoiceId().equalsIgnoreCase("randomblockplace")) {
                    if (player.getRandom().nextBetween(1, 50) == 1) {
                        var list = new ArrayList<BlockPos>();

                        for (Direction value : Direction.values()) {
                            BlockPos offset = pos.offset(value);
                            if (state.canPlaceAt(world, offset) && world.getBlockState(offset).isAir()) {
                                    list.add(offset);
                            }

                        }


                        if (!list.isEmpty()) {
                            BlockPos pos1 = list.get(player.getRandom().nextInt(list.size()));


                            world.setBlockState(pos1, state);

                            return EventResult.interruptFalse();
                        }

                    }

                }
            }
        }





        return EventResult.pass();
    }


    /**
     * Called every server tick. Loops over all players.
     */
    private static void onServerTick(MinecraftServer server) {
        Iterator<ScheduledGrow> iterator = growTasks.iterator();
        while (iterator.hasNext()) {
            ScheduledGrow task = iterator.next();
            ServerWorld world = task.world;
            BlockState state = world.getBlockState(task.pos);
            try {
                if (state.getBlock() instanceof Fertilizable fertilizable) {
                    for (int i = 0; i < 15; i++) {
                        BlockState currentState = world.getBlockState(task.pos);
                        if (fertilizable.isFertilizable(world, task.pos, currentState) && fertilizable.canGrow(world, world.random, task.pos, currentState)) {
                            fertilizable.grow(world, world.random, task.pos, currentState);

                        } else {
                            continue;
                        }
                    }
                }
                task.remaining--;
                if (task.remaining <= 0) {
                    iterator.remove();
                }
            } catch (Exception ignored) {

            }
        }

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



    private static class ScheduledGrow {
        final ServerWorld world;
        final BlockPos pos;
        int remaining;

        ScheduledGrow(ServerWorld world, BlockPos pos, int remaining) {
            this.world = world;
            this.pos = pos.toImmutable();
            this.remaining = remaining;
        }
    }
}