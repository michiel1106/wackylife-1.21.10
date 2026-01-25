package bikerboys.wackylife.entity.snail;


import net.minecraft.client.render.entity.*;
import net.minecraft.util.*;



public class SnailRenderer extends AgeableMobEntityRenderer<Snail, SnailRenderState, SnailModel> {
    public SnailRenderer(EntityRendererFactory.Context context) {
        super(context, new SnailModel(context.getPart(SnailModel.SNAIL)), new SnailModel(context.getPart(SnailModel.SNAIL)), 0.35f);
    }

    @Override
    public SnailRenderState createRenderState() {
        return new SnailRenderState();
    }

    @Override
    public Identifier getTexture(SnailRenderState state) {
        return Snail.DEFAULT_TEXTURE;
    }


    @Override
    public void updateRenderState(Snail snail, SnailRenderState state, float f) {
        super.updateRenderState(snail, state, f);
        state.walkAnimationState.copyFrom(snail.clientData.walkAnimationState);
        state.glideAnimationState.copyFrom(snail.clientData.glideAnimationState);
        state.flyAnimationState.copyFrom(snail.clientData.flyAnimationState);
        state.stopFlyAnimationState.copyFrom(snail.clientData.stopFlyAnimationState);
        state.startFlyAnimationState.copyFrom(snail.clientData.startFlyAnimationState);
        state.idleAnimationState.copyFrom(snail.clientData.idleAnimationState);
        state.attacking = snail.isSnailAttacking();
        state.flying = snail.isSnailFlying();
        state.gliding = snail.isSnailGliding();
        state.landing = snail.isSnailLanding();
        state.mining = snail.isSnailMining();
        state.fromTrivia = snail.isFromTrivia();
        state.skinName = snail.getSkinName();
        state.boundPlayerDead = snail.isBoundPlayerDead();
    }
}

