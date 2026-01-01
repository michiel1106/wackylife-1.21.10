package bikerboys.wackylife.attachements;

import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.server.network.*;

import java.util.function.*;

public interface SyncPredicate extends BiPredicate<AttachmentTarget, ServerPlayerEntity> {
    /**
     * @return a predicate that syncs an attachment with all clients
     */
    static AttachmentSyncPredicate allIfNotDead() {
        return (t, p) -> !p.isDead();
    }


}
