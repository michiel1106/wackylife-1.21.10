package bikerboys.wackylife.mixin;

import bikerboys.wackylife.util.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.*;
import net.minecraft.server.network.*;
import net.minecraft.sound.*;
import org.spongepowered.asm.mixin.*;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IServerPlayer {

    @Unique @Override
    public void ls$playNotifySound(SoundEvent sound, SoundCategory soundSource, float volume, float pitch) {
        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;

        self.networkHandler
                .sendPacket(
                        new PlaySoundS2CPacket(
                                Registries.SOUND_EVENT.getEntry(sound), soundSource, self.getX(), self.getY(), self.getZ(), volume, pitch, self.getRandom().nextLong()
                        )
                );

    }

}
