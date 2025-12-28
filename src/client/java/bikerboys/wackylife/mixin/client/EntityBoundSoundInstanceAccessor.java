package bikerboys.wackylife.mixin.client;

import net.minecraft.client.sound.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(value = EntityTrackingSoundInstance.class, priority = 1)
public interface EntityBoundSoundInstanceAccessor {
    @Accessor("entity")
    Entity getEntity();
}
