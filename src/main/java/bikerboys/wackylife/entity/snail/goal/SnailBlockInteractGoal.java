package bikerboys.wackylife.entity.snail.goal;


import bikerboys.wackylife.entity.snail.*;
import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.registry.tag.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

@SuppressWarnings("resource")
public final class SnailBlockInteractGoal extends Goal {

    @NotNull
    private final Snail mob;

    public SnailBlockInteractGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        if (mob.isPaused()) return false;

        BlockPos blockPos = mob.getBlockPos();

        BlockPos blockBelow = blockPos.down();
        return isTrapdoor(blockBelow) && isTrapdoorOpen(blockBelow);
    }

    @Override
    public void start() {
        BlockPos blockPos = mob.getBlockPos();
        //openTrapdoor(blockPos, true);

        BlockPos blockBelow = blockPos.down();
        openTrapdoor(blockBelow);
    }

    @Override
    public void tick() {
        start();
    }

    private boolean isTrapdoor(BlockPos blockPos) {
        BlockState blockState = getBlockState(blockPos);
        return blockState.isIn(BlockTags.TRAPDOORS);
    }

    private boolean isTrapdoorOpen(BlockPos blockPos) {
        return mob.getEntityWorld().getBlockState(blockPos).get(TrapdoorBlock.OPEN);
    }

    private void openTrapdoor(BlockPos blockPos) {
        if (!isTrapdoor(blockPos)) return;
        if (!isTrapdoorOpen(blockPos)) return;
        mob.getEntityWorld().setBlockState(blockPos, mob.getEntityWorld().getBlockState(blockPos).with(TrapdoorBlock.OPEN, false));
    }

    private BlockState getBlockState(BlockPos blockPos) {
        World level = mob.getEntityWorld();
        return level.getBlockState(blockPos);
    }
}
