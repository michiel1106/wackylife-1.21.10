package bikerboys.wackylife.commands;

import bikerboys.wackylife.*;
import bikerboys.wackylife.manager.*;
import bikerboys.wackylife.util.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class WackyCmdManager {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var series = WackyLife.wackyLife;
        var deaths = series.getDeathsManager();

        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("wackylife");

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
        root.then(CommandManager.literal("pausetoggle")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    Constants.paused = !Constants.paused;
                    return 1;
                })
        );

        // initialize (requires op)
        root.then(CommandManager.literal("initialize")
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
}
