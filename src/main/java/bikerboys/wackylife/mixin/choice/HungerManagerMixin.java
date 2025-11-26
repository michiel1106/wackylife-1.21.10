package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Debug(export = true)
@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {

    @Shadow private float exhaustion;

    @Shadow private float saturationLevel;

    @Shadow private int foodTickTimer;

    @Shadow private int foodLevel;

    @Shadow public abstract void addExhaustion(float exhaustion);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void update(ServerPlayerEntity player) {
        ServerWorld serverWorld = player.getEntityWorld();
        Difficulty difficulty = serverWorld.getDifficulty();
        boolean hasChoice = ModAttachments.getChoice(player).negativeChoiceId().equals("double_hunger_drain");




        if (!hasChoice) {
            if (this.exhaustion > 4.0F) {
                this.exhaustion -= 4.0F;
                if (this.saturationLevel > 0.0F) {
                    this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
                } else if (difficulty != Difficulty.PEACEFUL) {
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
            }
        } else {
            if (this.exhaustion > 2.0F) {
                this.exhaustion -= 2.0F;
                if (this.saturationLevel > 0.0F) {
                    this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
                } else if (difficulty != Difficulty.PEACEFUL) {
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
            }
        }

        boolean bl = serverWorld.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
        if (bl && this.saturationLevel > 0.0F && player.canFoodHeal() && this.foodLevel >= 20) {
            this.foodTickTimer++;
            if (this.foodTickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0F);
                player.heal(f / 6.0F);
                this.addExhaustion(f);
                this.foodTickTimer = 0;
            }
        } else if (bl && this.foodLevel >= 18 && player.canFoodHeal()) {
            this.foodTickTimer++;
            if (this.foodTickTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(6.0F);
                this.foodTickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            this.foodTickTimer++;
            if (this.foodTickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.damage(serverWorld, player.getDamageSources().starve(), 1.0F);
                }

                this.foodTickTimer = 0;
            }
        } else {
            this.foodTickTimer = 0;
        }
    }
}
