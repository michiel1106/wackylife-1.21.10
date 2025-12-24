package bikerboys.wackylife.util;

import net.minecraft.sound.*;

public interface IServerPlayer {
    String error = "This method should be overridden in the mixin";

    default boolean ls$isWatcher() { throw new UnsupportedOperationException(error); }

    default void ls$playNotifySound(SoundEvent sound, SoundCategory soundSource, float volume, float pitch) { throw new UnsupportedOperationException(error); }
}
