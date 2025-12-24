package bikerboys.wackylife.wyr.choice;

import bikerboys.wackylife.wyr.actualchoicesfr.choiceTemplates.*;
import bikerboys.wackylife.wyr.actualchoicesfr.negative.*;
import bikerboys.wackylife.wyr.actualchoicesfr.positive.*;
import net.minecraft.block.*;
import net.minecraft.component.*;
import net.minecraft.component.type.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.server.world.*;
import net.minecraft.stat.*;
import net.minecraft.util.math.*;

import java.util.*;
import java.util.stream.*;

public class ChoiceRegistry {

    private static final Map<String, Choice> CHOICES = new HashMap<>();

    private static final List<Choice> POSITIVE_CHOICES = new ArrayList<>();
    private static final List<Choice> NEGATIVE_CHOICES = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static void registerChoices() {


        register(new Choice("double_mob_drops", true)); // done
        register(new Choice("no_fire_damage", true)); // done
        register(new AttributeChoice("extra_heart", true, EntityAttributes.MAX_HEALTH, 2, EntityAttributeModifier.Operation.ADD_VALUE)); // done
        register(new Choice("instant_bonemeal", true)); // done
        register(new InstantBreeding("instant_breeding", true)); // done
        register(new FasterSwimming("faster_swimming", true)); // done
        register(new LessFallDamage("reduced_fall_damage", true)); // done
        register(new Choice("faster_ladder_climbing", true)); // done
        register(new Choice("faster_furnaces", true)); // done
        register(new Choice("faster_eating", true)); // done
        register(new Shorter("shorter", true)); // done
        register(new MoreKnockback("moreknockback", true)); // done
        register(new Choice("infinite_shearing", true)); // done
        register(new MaxDurability("max_durability", true)); // done
        register(new OnAddChoice("receivemace", true, (player, world, data) -> player.giveOrDropStack(new ItemStack(Items.MACE)))); // done
        register(new SetAttributeChoice("stepheight", true, EntityAttributes.STEP_HEIGHT, 1.1d)); // done
        register(new PotionEffectChoice("slowfalling", true, StatusEffects.SLOW_FALLING, (player -> player.fallDistance >= 3))); // done
        register(new OnAddChoice("receivetotem", true, (player, world, data) -> player.giveOrDropStack(new ItemStack(Items.TOTEM_OF_UNDYING)))); // done
        register(new SimpleTickChoice("photosynthesis", true, (player, world, nbtCompound) -> {
            int delay = nbtCompound.getInt("delay", 80);delay -= 1;nbtCompound.putInt("delay", delay);
            if (delay <= 0) {nbtCompound.putInt("delay", 80);if (world.isSkyVisible(player.getBlockPos())) {player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 1, 0,  false, false));}}
        })); // done
        register(new Choice("ice_physics", true)); // done
        register(new Choice("infinite_pearls", true)); // done
        register(new JesusChoice("jesus", true)); // done
        register(new Choice("infinite_water_bucket", true)); // done
        register(new OnAddChoice("receive_diamonds", true, (player, world, data) -> player.giveOrDropStack(new ItemStack(Items.DIAMOND, 15)))); // done
        register(new OnAddChoice("receive_chestplate", true, (player, world, data) -> player.giveOrDropStack(new ItemStack(Items.DIAMOND_CHESTPLATE, 1)))); // done
        register(new OnAddChoice("receive_golden_apples", true, (player, world, data) -> player.giveOrDropStack(new ItemStack(Items.GOLDEN_APPLE, 5)))); // done
        register(new OnAddChoice("receive_elytra", true, (player, world, data) -> {ItemStack itemStack = new ItemStack(Items.ELYTRA, 1);itemStack.set(DataComponentTypes.MAX_DAMAGE, 30);player.giveOrDropStack(itemStack);})); // done
        register(new OnAddChoice("receive_enchanted_golden_apples", true, (player, world, data) -> player.giveOrDropStack(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1)))); // done
        register(new OnAddChoice("receive_crystals", true, (player, world, data) -> player.giveOrDropStack(new ItemStack(Items.END_CRYSTAL, 4)))); // done
        register(new PotionEffectChoice("regeneration", true, StatusEffects.REGENERATION, player -> true)); // done


        register(new Choice("always_phantom", false)); // done
        register(new Choice("double_hunger_drain", false)); // done
        register(new AggressiveEnderman("aggressive_enderman", false)); // done
        register(new OccasionalDrops("occasional_drops", false)); // done
        register(new RandomSprint("random_sprint", false)); // done
        register(new Choice("thick_fog", false)); // done
        register(new Choice("no_water_collision", false)); // done
        register(new RandomBlockPlace("randomblockplace", false)); // done
        register(new LowerJumpHeight("lowerjump", false)); // done
        register(new SilverFishSpawn("silverfishspawn", false)); // done
        register(new HigherAttackCooldown("higherattackcooldown", false)); // done
        register(new NoArmorAboveDiamond("noarmorabovediamond", false)); // done
        register(new ImmediateBoatSink("immediatesink", false)); // done
        register(new Choice("hotbarfov", false)); // done
        register(new Choice("hiddenhealth", false)); // done
        register(new Explosion("explode", false)); // done
        register(new Choice("offsetcrosshair", false)); // done
        register(new SimpleTickChoice("cantuseshield", false, ((player, world, nbtCompound) -> {
            if (player.getMainHandStack().isOf(Items.SHIELD)) {player.getMainHandStack().decrement(1);player.dropStack((ServerWorld) world, new ItemStack(Items.SHIELD));}
            if (player.getOffHandStack().isOf(Items.SHIELD)) {player.getOffHandStack().decrement(1);player.dropStack((ServerWorld) world, new ItemStack(Items.SHIELD));}
        }))); // done
        register(new PotionEffectChoice("glowingeffect", false, StatusEffects.GLOWING, (player) -> true)); // done
        register(new SimpleTickChoice("waterhurts", false, ((player, world, nbtCompound) -> {if (player.isTouchingWaterOrRain()) {player.damage((ServerWorld) world, player.getDamageSources().magic(), 2.0f);}}))); // done
        register(new SimpleTickChoice("rainhurts", false, ((player, world, nbtCompound) -> {if (isBeingRainedOn(player)) {player.damage((ServerWorld) world, player.getDamageSources().magic(), 2.0f);}}))); // done
        register(new SimpleTickChoice("grasshurts", false, ((player, world, nbtCompound) -> {if (player.getSteppingBlockState().isOf(Blocks.GRASS_BLOCK)) {player.damage((ServerWorld) world, player.getDamageSources().magic(), 2.0f);}}))); // done
        register(new SimpleTickChoice("burnindaylight", false, ((player, world, nbtCompound) -> {if (player.getEntityWorld().isSkyVisible(player.getBlockPos()) && player.getEntityWorld().isDay()) {player.damage((ServerWorld) world, player.getDamageSources().onFire(), 2.0f); player.setOnFireFor(4.5f);}}))); // done
        register(new Choice("pacifist", false)); // done
        register(new Choice("half_hunger", false)); // done
        register(new FleeingItemChoice("fleeing_items", false)); // done
        register(new SimpleTickChoice("blindness", false, ((player, world, nbtCompound) -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40, 0, false, false))))); // done
        register(new AttributeChoice("lose_2_hearts", false, EntityAttributes.MAX_HEALTH, -4, EntityAttributeModifier.Operation.ADD_VALUE)); // done
        register(new PotionEffectChoice("hunger", false, StatusEffects.HUNGER, player -> true)); // done
        register(new PotionEffectChoice("slowness", false, StatusEffects.SLOWNESS, player -> true, 1)); // done


        register(new Choice("empty_pos", true));
        register(new Choice("empty_neg", false));


        POSITIVE_CHOICES.clear();
        NEGATIVE_CHOICES.clear();
        for (Choice choice : CHOICES.values()) {
            if (choice.isPositive()) {
                if (!choice.getId().equals("empty_pos")) {
                    POSITIVE_CHOICES.add(choice);
                }
            } else {
                if (!choice.getId().equals("empty_neg")) {
                    NEGATIVE_CHOICES.add(choice);
                }
            }
        }

    }


    static boolean isBeingRainedOn(PlayerEntity player) {
        BlockPos blockPos = player.getBlockPos();
        return player.getEntityWorld().hasRain(blockPos)
                || player.getEntityWorld().hasRain(BlockPos.ofFloored(blockPos.getX(), player.getBoundingBox().maxY, blockPos.getZ()));
    }


    /**
     * Registers a new choice definition.
     * Call this from your mod's main initializer.
     * @return The choice that was passed in, for convenience.
     */
    public static Choice register(Choice choice) {
        if (CHOICES.containsKey(choice.getId())) {
            throw new IllegalArgumentException("Duplicate choice ID: " + choice.getId());
        }
        CHOICES.put(choice.getId(), choice);
        return choice;
    }

    /**
     * Gets a choice definition from its ID.
     * @return The Choice object, or null if not found.
     */
    public static Choice get(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return CHOICES.get(id);
    }

    // --- NEW METHODS FOR COMMANDS ---

    /**
     * @return A Set of all registered positive choice IDs.
     */
    public static Set<String> getPositiveChoiceIds() {
        return CHOICES.values().stream()
                .filter(Choice::isPositive)
                .map(Choice::getId)
                .collect(Collectors.toSet());
    }

    /**
     * @return A Set of all registered negative choice IDs.
     */
    public static Set<String> getNegativeChoiceIds() {
        return CHOICES.values().stream()
                .filter(choice -> !choice.isPositive())
                .map(Choice::getId)
                .collect(Collectors.toSet());
    }


    public static List<ChoicePair> getRandomChoicePairs() {
        // --- Safety Check ---
        // We need at least 2 of each type to provide unique pairs.
        if (POSITIVE_CHOICES.size() < 2 || NEGATIVE_CHOICES.size() < 2) {
            System.err.println("WackyLife Error: Not enough choices registered to create 2 unique pairs!");
            // Return an empty list to prevent a crash
            return Collections.emptyList();
        }

        // --- Get 2 unique positive choices ---
        Choice pos1 = POSITIVE_CHOICES.get(RANDOM.nextInt(POSITIVE_CHOICES.size()));
        Choice pos2;
        do {
            pos2 = POSITIVE_CHOICES.get(RANDOM.nextInt(POSITIVE_CHOICES.size()));
        } while (pos1.equals(pos2)); // Ensure they are not the same

        // --- Get 2 unique negative choices ---
        Choice neg1 = NEGATIVE_CHOICES.get(RANDOM.nextInt(NEGATIVE_CHOICES.size()));
        Choice neg2;
        do {
            neg2 = NEGATIVE_CHOICES.get(RANDOM.nextInt(NEGATIVE_CHOICES.size()));
        } while (neg1.equals(neg2)); // Ensure they are not the same

        // --- Create and return the pairs ---
        ChoicePair pair1 = new ChoicePair(pos1.getId(), neg1.getId());
        ChoicePair pair2 = new ChoicePair(pos2.getId(), neg2.getId());

        return Arrays.asList(pair1, pair2);
    }

    // Example of how you would register choices in your main mod class:
    // public static final Choice FLY_POSITIVE = ChoiceRegistry.register(new Choice("wackylife:fly", true));
    // public static final Choice NO_HUNGER_NEGATIVE = ChoiceRegistry.register(new Choice("wackylife:no_hunger", false));
}
