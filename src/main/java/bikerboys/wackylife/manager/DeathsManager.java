package bikerboys.wackylife.manager;

import bikerboys.wackylife.series.WackySeries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeathsManager {
    private final List<String> deadPlayerNames = new ArrayList<>();
    private final List<ClaimRequest> pendingClaims = new ArrayList<>();
    private final WackySeries series;

    public DeathsManager(WackySeries series) {
        this.series = series;
    }

    public void registerDeath(ServerPlayerEntity player) {
        String name = player.getName().getString();
        if (!deadPlayerNames.contains(name)) {
            deadPlayerNames.add(name);
        }
    }

    public void claimDeath(ServerPlayerEntity claimer, String targetName, MinecraftServer server) {
        if (!deadPlayerNames.contains(targetName)) {
            claimer.sendMessage(Text.literal("§cThat player is not marked as dead."), false);
            return;
        }

        ClaimRequest claim = new ClaimRequest(claimer.getName().getString(), targetName);
        pendingClaims.add(claim);

        for (ServerPlayerEntity op : server.getPlayerManager().getPlayerList()) {
            if (op.hasPermissionLevel(2)) {
                op.sendMessage(Text.literal("§eClaim request: " + claimer.getName().getString() + " claims the kill on " + targetName), false);
                op.sendMessage(Text.literal("§7Use §a/wackylife approve " + claimer.getName().getString() + " " + targetName + "§7 to approve."), false);
            }
        }

        claimer.sendMessage(Text.literal("§aClaim sent for approval."), false);
    }

    public void approveClaim(String claimerName, String targetName, MinecraftServer server) {
        Iterator<ClaimRequest> iterator = pendingClaims.iterator();
        while (iterator.hasNext()) {
            ClaimRequest request = iterator.next();
            if (request.claimer.equals(claimerName) && request.target.equals(targetName)) {
                iterator.remove();
                deadPlayerNames.remove(targetName);
                onClaimApproved(claimerName, targetName, server);
                break;
            }
        }
    }

    private void onClaimApproved(String claimerName, String targetName, MinecraftServer server) {
        ServerPlayerEntity claimer = server.getPlayerManager().getPlayer(claimerName);
        ServerPlayerEntity target = server.getPlayerManager().getPlayer(targetName);

        if (claimer == null || target == null) return;
        series.onPlayerKill(claimer, target, claimer.getEntityWorld());
    }

    public List<String> getDeadPlayerNames() {
        return deadPlayerNames;
    }

    public List<ClaimRequest> getPendingClaims() {
        return pendingClaims;
    }

    public static class ClaimRequest {
        private final String claimer;
        private final String target;

        public ClaimRequest(String claimer, String target) {
            this.claimer = claimer;
            this.target = target;
        }

        public String getClaimer() { return claimer; }
        public String getTarget() { return target; }
    }
}
