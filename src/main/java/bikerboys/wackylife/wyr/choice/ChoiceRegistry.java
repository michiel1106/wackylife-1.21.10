package bikerboys.wackylife.wyr.choice;

import bikerboys.wackylife.wyr.actualchoicesfr.*;

import java.util.*;
import java.util.stream.*;

public class ChoiceRegistry {

    private static final Map<String, Choice> CHOICES = new HashMap<>();

    public static void registerChoices() {
        register(new JumpBoostChoice());
        register(new Choice("empty_pos", true));
        register(new Choice("empty_neg", false));

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

    // Example of how you would register choices in your main mod class:
    // public static final Choice FLY_POSITIVE = ChoiceRegistry.register(new Choice("wackylife:fly", true));
    // public static final Choice NO_HUNGER_NEGATIVE = ChoiceRegistry.register(new Choice("wackylife:no_hunger", false));
}
