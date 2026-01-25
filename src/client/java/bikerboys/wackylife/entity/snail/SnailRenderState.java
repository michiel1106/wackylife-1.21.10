package bikerboys.wackylife.entity.snail;


import net.minecraft.client.render.entity.state.*;
import net.minecraft.entity.*;

public class SnailRenderState extends LivingEntityRenderState {
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState glideAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState stopFlyAnimationState = new AnimationState();
    public final AnimationState startFlyAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public boolean attacking;
    public boolean flying;
    public boolean gliding;
    public boolean landing;
    public boolean mining;
    public boolean fromTrivia;
    public String skinName;
    public boolean boundPlayerDead;
}

