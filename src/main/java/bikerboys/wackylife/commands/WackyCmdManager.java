package bikerboys.wackylife.commands;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import bikerboys.wackylife.attachements.*;
import bikerboys.wackylife.manager.*;
import bikerboys.wackylife.series.*;
import bikerboys.wackylife.util.*;
import bikerboys.wackylife.wyr.choice.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.*;

public class WackyCmdManager {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var series = WackyLife.wackyLife;
        var deaths = series.getDeathsManager();

        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("wackylife");

        LiteralArgumentBuilder<ServerCommandSource> choiceCommand = CommandManager.literal("choice")
                // Require permission level 2 (like /gamemode, /give)
                .requires(source -> source.hasPermissionLevel(2));

        // --- /wackylife choice set <player> <positive_choice> <negative_choice> ---
        choiceCommand.then(CommandManager.literal("set")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("positive_choice", StringArgumentType.string())
                                // Suggests only positive choices
                                .suggests((context, builder) ->
                                        CommandSource.suggestMatching(ChoiceRegistry.getPositiveChoiceIds(), builder))
                                .then(CommandManager.argument("negative_choice", StringArgumentType.string())
                                        // Suggests only negative choices
                                        .suggests((context, builder) ->
                                                CommandSource.suggestMatching(ChoiceRegistry.getNegativeChoiceIds(), builder))
                                        .executes(context -> setChoice(context.getSource(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "positive_choice"),
                                                StringArgumentType.getString(context, "negative_choice")
                                        ))
                                )
                        )
                )
        );

        // --- /wackylife choice list <player> ---
        choiceCommand.then(CommandManager.literal("list")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> listChoices(context.getSource(),
                                EntityArgumentType.getPlayer(context, "player")
                        ))
                )
        );

        // --- /wackylife choice remove <player> ---
        choiceCommand.then(CommandManager.literal("remove")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> removeChoices(context.getSource(),
                                EntityArgumentType.getPlayer(context, "player")
                        ))
                )
        );

        // Add the new /choice commands to the /wackylife root
        root.then(choiceCommand);

        // claimkill <target> (anyone can run)
        root.then(CommandManager.literal("claimkill")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .suggests((context, builder) ->
                                CommandSource.suggestMatching(deaths.getDeadPlayerNames(), builder)
                        )
                        .executes(context -> {
                            ServerPlayerEntity claimer = context.getSource().getPlayer();
                            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                            deaths.claimDeath(claimer, target.getName().getString(), context.getSource().getServer());
                            return 1;
                        }))
        );

        root.then(CommandManager.literal("wildcard")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("wackyskins")
                                .then(CommandManager.literal("peek")
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                    String realName = player.getName().getString();
                                                    Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();

                                                    if (wildcardObj instanceof WackySkins wackySkins) {
                                                        String fakeName = wackySkins.playerNameMap.get(realName);

                                                        if (fakeName != null) {
                                                            ctx.getSource().getPlayer().sendMessage(
                                                                    Text.literal("The player's fake identity is ")
                                                                            .append(Text.literal(fakeName))
                                                                            .append(Text.literal(", they are actually: "))
                                                                            .append(Text.literal(realName))
                                                            );
                                                        } else {
                                                            ctx.getSource().getPlayer().sendMessage(
                                                                    Text.literal(realName)
                                                                            .append(Text.literal(" does not currently have a fake identity."))
                                                            );
                                                        }
                                                    } else {
                                                        ctx.getSource().getPlayer().sendMessage(Text.literal("Wacky skins is currently not active."));
                                                    }

                                                    return 1;
                                                })))));

        root.then(CommandManager.literal("wildcard")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("wildcard", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(Arrays.stream(WildcardEnum.values()).map((WildcardEnum::asString)).toList(), builder))
                                .executes(context -> {
                                    String string = StringArgumentType.getString(context, "wildcard");
                                    WackyLife.wackyLife.setWildcard(string, context.getSource().getServer());
                                    return 1;
                                })))
        );

        root.then(CommandManager.literal("wildcard")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("wackyskins")
                        .then(CommandManager.literal("swap")
                                .executes(context -> {
                                    Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();
                                    if (wildcardObj instanceof WackySkins wackySkins) {
                                        wackySkins.swapPlayerList(context.getSource().getServer());
                                    }

                                    return 1;
                                }))
                        .then(CommandManager.literal("time")
                                .executes(context -> {
                                    if (WackyLife.wackyLife.getWildcardObj() instanceof WackySkins wackySkins) {
                                        context.getSource().getPlayer().sendMessage(Text.literal("Current cooldown for the next swap: " + wackySkins.tickDelay));
                                    } else {
                                        context.getSource().getPlayer().sendMessage(Text.literal("Current wildcard isnt wacky skins or isnt activated. Please activate it first."));
                                    }
                                    return 1;
                                })
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("time", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int time = IntegerArgumentType.getInteger(context, "time");

                                            Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();
                                            if (wildcardObj instanceof WackySkins skins) {
                                                skins.tickDelay = time;
                                            }


                                            return time;
                                        }))))));

        root.then(CommandManager.literal("wildcard")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("get")
                        .executes(context -> {
                            String wildcard = WackyLife.wackyLife.getWildcard();
                            if (wildcard != null) {
                                context.getSource().getPlayer().sendMessage(Text.literal("Current active wildcard is: " + wildcard));
                            } else {
                                context.getSource().getPlayer().sendMessage(Text.literal("There is currently no wildcard active. Activate one with /wackylife wildcard set {wildcard}"));
                            }

                            return 1;
                        })));

        root.then(CommandManager.literal("approve")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("claimer", EntityArgumentType.player())
                        .suggests((context, builder) ->
                                CommandSource.suggestMatching(
                                        deaths.getPendingClaims().stream()
                                                .map(DeathsManager.ClaimRequest::getClaimer)
                                                .toList(), builder
                                )
                        )
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                                .suggests((context, builder) ->
                                        CommandSource.suggestMatching(
                                                deaths.getPendingClaims().stream()
                                                        .map(DeathsManager.ClaimRequest::getTarget)
                                                        .toList(), builder
                                        )
                                )
                                .executes(context -> {
                                    ServerPlayerEntity claimer = EntityArgumentType.getPlayer(context, "claimer");
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
                                    deaths.approveClaim(
                                            claimer.getName().getString(),
                                            target.getName().getString(),
                                            context.getSource().getServer()
                                    );
                                    context.getSource().sendFeedback(() ->
                                            Text.literal("Approved claim: " + claimer.getName().getString() +
                                                    " → " + target.getName().getString()), false);
                                    return 1;
                                }))
                )
        );

        root.then(CommandManager.literal("time")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("time", WackyTimeArgumentType.time())
                                .executes(context -> {
                                    int time = IntegerArgumentType.getInteger(context, "time");
                                    var scoreboard = ScoreboardManager.INSTANCE.getScoreboard("time", context.getSource().getServer(), Constants.SessionTime);
                                    if (scoreboard != null) scoreboard.setScore(time);
                                    return time;
                                })
                                .suggests((ctx, builder) -> CommandSource.suggestMatching(new String[]{"1t","1s","1m","1h","1d"}, builder))
                        )
                )
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("time", WackyTimeArgumentType.time())
                                .executes(context -> {
                                    int time = IntegerArgumentType.getInteger(context, "time");
                                    var scoreboard = ScoreboardManager.INSTANCE.getScoreboard("time", context.getSource().getServer(), Constants.SessionTime);
                                    if (scoreboard != null) scoreboard.incrementScore(time);
                                    return time;
                                })
                                .suggests((ctx, builder) -> CommandSource.suggestMatching(new String[]{"1t","1s","1m","1h","1d"}, builder))
                        )
                )
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("time", WackyTimeArgumentType.time())
                                .executes(context -> {
                                    int time = IntegerArgumentType.getInteger(context, "time");
                                    var scoreboard = ScoreboardManager.INSTANCE.getScoreboard("time", context.getSource().getServer(), Constants.SessionTime);
                                    if (scoreboard != null) scoreboard.incrementScore(-time);
                                    return time;
                                })
                                .suggests((ctx, builder) -> CommandSource.suggestMatching(new String[]{"1t","1s","1m","1h","1d"}, builder))
                        )
                )
        );

        // pausetoggle (requires op)
        root.then(CommandManager.literal("pause")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    Constants.paused = !Constants.paused;

                    if (Constants.paused) {
                        context.getSource().getPlayer().sendMessage(Text.literal("Session is now paused!"));
                    } else {
                        context.getSource().getPlayer().sendMessage(Text.literal("Session has been resumed!"));
                    }

                    return 1;
                })
        );

        // initialize (requires op)
        root.then(CommandManager.literal("start")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    WackyLife.wackyLife.initialize(context.getSource().getWorld());
                    return 1;
                })
        );

        // lives set <players> <lives> (requires op)
        root.then(CommandManager.literal("lives")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("players", EntityArgumentType.players())
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("lives", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
                                            int lives = IntegerArgumentType.getInteger(context, "lives");

                                            for (ServerPlayerEntity player : players) {
                                                var scoreboard = ScoreboardManager.INSTANCE.getScoreboard(player, context.getSource().getServer(), Constants.LivesScoreboard);
                                                if (scoreboard != null) {
                                                    scoreboard.setScore(lives);
                                                }
                                            }
                                            return players.size();
                                        }))))
        );

        dispatcher.register(root);
    }



    private static int setChoice(ServerCommandSource source, ServerPlayerEntity player, String posId, String negId) {
        Choice posChoice = ChoiceRegistry.get(posId);
        Choice negChoice = ChoiceRegistry.get(negId);

        // --- Error Handling ---
        if (posChoice == null) {
            source.sendError(Text.literal("Error: Positive choice ID not found: " + posId));
            return 0; // Returning 0 signifies failure
        }
        if (negChoice == null) {
            source.sendError(Text.literal("Error: Negative choice ID not found: " + negId));
            return 0;
        }
        if (!posChoice.isPositive()) {
            source.sendError(Text.literal("Error: " + posId + " is not a positive choice."));
            return 0;
        }
        if (negChoice.isPositive()) {
            source.sendError(Text.literal("Error: " + negId + " is not a negative choice."));
            return 0;
        }

        // --- Success ---
        // Use the ChoiceManager to safely set the choice
        ChoiceManager.setChoice(player, posChoice, negChoice);
        source.sendFeedback(() -> Text.literal("Set " + player.getName().getString() + "'s choices to " + posId + " and " + negId), true);
        return 1; // Returning 1 signifies success
    }

    // Handler logic for "list"
    private static int listChoices(ServerCommandSource source, ServerPlayerEntity player) {
        ChoiceAttachment attachment = ModAttachments.getChoice(player);
        String posId = attachment.positiveChoiceId().isEmpty() ? "none" : attachment.positiveChoiceId();
        String negId = attachment.negativeChoiceId().isEmpty() ? "none" : attachment.negativeChoiceId();

        source.sendFeedback(() -> Text.literal(player.getName().getString() + "'s choices:")
                .append(Text.literal("\n - Positive: " + posId))
                .append(Text.literal("\n - Negative: " + negId)), false); // false = don't broadcast to all ops
        return 1;
    }

    // Handler logic for "remove"
    private static int removeChoices(ServerCommandSource source, ServerPlayerEntity player) {
        // Use the ChoiceManager to safely clear choices
        ChoiceManager.clearChoices(player);
        source.sendFeedback(() -> Text.literal("Cleared all choices for " + player.getName().getString()), true);
        return 1;
    }
}
