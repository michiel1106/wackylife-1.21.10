package bikerboys.wackylife.mixin.choice;


import bikerboys.wackylife.attachements.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.fluid.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.stat.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import net.minecraft.world.spawner.*;
import org.objectweb.asm.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;


@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {

    @Shadow private int cooldown;




    @Inject(
            method = "spawn",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/spawner/PhantomSpawner;cooldown:I",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            ), cancellable = true
    )
    public void spawnthang(ServerWorld world, boolean spawnMonsters, CallbackInfo ci) {
                Random random = world.random;
                if (this.cooldown <= 0) {
                    if (world.getAmbientDarkness() >= 5 || !world.getDimension().hasSkyLight()) {
                        for (ServerPlayerEntity serverPlayerEntity : world.getPlayers()) {
                            boolean hasChoice = ModAttachments.getChoice(serverPlayerEntity).negativeChoiceId().equals("always_phantom");
                            if (!hasChoice) {
                                continue;
                            }
                            if (!serverPlayerEntity.isSpectator()) {
                                BlockPos blockPos = serverPlayerEntity.getBlockPos();
                                if (!world.getDimension().hasSkyLight() || blockPos.getY() >= world.getSeaLevel() && world.isSkyVisible(blockPos)) {
                                    LocalDifficulty localDifficulty = world.getLocalDifficulty(blockPos);
                                    if (localDifficulty.isHarderThan(random.nextFloat() * 3.0F)) {
                                        ServerStatHandler serverStatHandler = serverPlayerEntity.getStatHandler();

                                        int i = random.nextBetween(120000, 190000);
                                        int j = 24000;
                                        int i1 = random.nextInt(i);

                                        if (i1 >= 72000) {
                                            BlockPos blockPos2 = blockPos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                                            BlockState blockState = world.getBlockState(blockPos2);
                                            FluidState fluidState = world.getFluidState(blockPos2);
                                            if (SpawnHelper.isClearForSpawn(world, blockPos2, blockState, fluidState, EntityType.PHANTOM)) {
                                                EntityData entityData = null;
                                                int k = 1 + random.nextInt(localDifficulty.getGlobalDifficulty().getId() + 1);

                                                for (int l = 0; l < k; l++) {
                                                    PhantomEntity phantomEntity = EntityType.PHANTOM.create(world, SpawnReason.NATURAL);
                                                    if (phantomEntity != null) {
                                                        phantomEntity.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
                                                        entityData = phantomEntity.initialize(world, localDifficulty, SpawnReason.NATURAL, entityData);
                                                        world.spawnEntityAndPassengers(phantomEntity);
                                                        this.cooldown = this.cooldown + (60 + random.nextInt(60)) * 20;
                                                        ci.cancel();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

}
