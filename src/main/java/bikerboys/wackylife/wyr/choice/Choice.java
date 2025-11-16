package bikerboys.wackylife.wyr.choice;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * This is the DEFINITION of a choice. It holds no player-specific data.
 * All methods that need data will be GIVEN the player's data tag from
 * the ChoiceAttachment.
 */
public class Choice {
    private final String id;
    private @Nullable String translationKey;
    protected final boolean positive;

    // You need to pass an ID in the constructor to register it
    public Choice(String id, boolean positive) {
        this.id = id;
        this.positive = positive;
    }

    public final String getId() {
        return this.id;
    }

    public final String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("choice", Identifier.of(this.getId())) + ".description";
        }
        return this.translationKey;
    }

    public String toString() {
        return this.getId();
    }

    public boolean isStackable() {
        return false;
    }

    public final boolean isPositive() {
        return this.positive;
    }

    /**
     * Called when the choice is first added to the player.
     * Use this to set default data, like "cooldown = 0".
     * @param data The mutable data tag for this choice from the player's attachment.
     */
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        // Example: data.putInt("cooldown", 0);
    }

    /**
     * Called when the choice is removed from the player.
     * @param data The data tag, in case you need to read a value one last time.
     */
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
    }

    /**
     * Called every server tick.
     * @param data The mutable data tag for this choice. Read and write your
     * cooldowns/bools here!
     */
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        // --- THIS IS THE SOLUTION ---
        // Example:
        // int cooldown = data.getInt("cooldown");
        // if (cooldown > 0) {
        //     data.putInt("cooldown", cooldown - 1);
        // } else {
        //     // Do the thing
        // }
    }

    public void onPlayerSneak(World world, PlayerEntity player, NbtCompound data) {
    }

    public void onPlayerUnsneak(World world, PlayerEntity player, NbtCompound data) {
    }
}