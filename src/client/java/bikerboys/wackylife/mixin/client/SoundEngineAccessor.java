package bikerboys.wackylife.mixin.client;

import com.google.common.collect.*;
import net.minecraft.client.sound.*;
import net.minecraft.sound.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SoundSystem.class, priority = 2)
public interface SoundEngineAccessor {

    @Accessor("sounds")
    Multimap<SoundCategory, SoundInstance> getSounds();
}
