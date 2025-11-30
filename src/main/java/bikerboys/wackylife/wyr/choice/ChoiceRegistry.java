package bikerboys.wackylife.wyr.choice;

import bikerboys.wackylife.wyr.actualchoicesfr.negative.*;
import bikerboys.wackylife.wyr.actualchoicesfr.positive.*;

import java.util.*;
import java.util.stream.*;

public class ChoiceRegistry {

    private static final Map<String, Choice> CHOICES = new HashMap<>();

    private static final List<Choice> POSITIVE_CHOICES = new ArrayList<>();
    private static final List<Choice> NEGATIVE_CHOICES = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static void registerChoices() {
        // 4/30 - positive
        // 6/30 - negative


        register(new Choice("double_mob_drops", true)); // done
        register(new Choice("no_fire_damage", true)); // done
        register(new ExtraHeart("extra_heart", true)); // done
        register(new Choice("instant_bonemeal", true)); // done
        register(new InstantBreeding("instant_breeding", true)); // done
        register(new FasterSwimming("faster_swimming", true)); // done
        register(new LessFallDamage("reduced_fall_damage", true)); // done
        register(new Choice("faster_ladder_climbing", true)); // done
        register(new Choice("faster_furnaces", true)); // done
        register(new Choice("faster_eating", true)); // done



        register(new Choice("always_phantom", false)); // done
        register(new Choice("double_hunger_drain", false)); // done
        register(new AggressiveEnderman("aggressive_enderman", false)); // done
        register(new OccasionalDrops("occasional_drops", false)); // done
        register(new RandomSprint("random_sprint", false)); // done
        register(new Choice("thick_fog", false)); // done




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
