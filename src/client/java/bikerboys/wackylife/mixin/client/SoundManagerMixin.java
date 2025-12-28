package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import net.minecraft.client.sound.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = SoundManager.class, priority = 1)
public class SoundManagerMixin {


    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"))
    private void play(SoundInstance sound, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        ClientSounds.onSoundPlay(sound);
    }

}
