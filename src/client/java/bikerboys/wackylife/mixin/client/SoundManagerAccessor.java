package bikerboys.wackylife.mixin.client;

import net.minecraft.client.sound.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(value = SoundManager.class, priority = 2)
public interface SoundManagerAccessor {

    @Accessor("soundSystem")
    SoundSystem getSoundSystem();
}
