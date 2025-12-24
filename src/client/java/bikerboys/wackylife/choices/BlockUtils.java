package bikerboys.wackylife.choices;


import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.registry.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;

import java.util.stream.*;

public enum BlockUtils
{
    ;

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public static BlockState getState(BlockPos pos)
    {
        return MC.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos)
    {
        return getState(pos).getBlock();
    }


    private static VoxelShape getOutlineShape(BlockPos pos)
    {
        return getState(pos).getCollisionShape(MC.world, pos);
    }


    public static BlockHitResult raycast(Vec3d from, Vec3d to,
                                         RaycastContext.FluidHandling fluidHandling)
    {
        RaycastContext context = new RaycastContext(from, to,
                RaycastContext.ShapeType.COLLIDER, fluidHandling, MC.player);

        return MC.world.raycast(context);
    }

    public static BlockHitResult raycast(Vec3d from, Vec3d to)
    {
        return raycast(from, to, RaycastContext.FluidHandling.NONE);
    }


    public static Stream<Box> getBlockCollisions(Box box)
    {
        Iterable<VoxelShape> blockCollisions =
                MC.world.getBlockCollisions(MC.player, box);

        return StreamSupport.stream(blockCollisions.spliterator(), false)
                .flatMap(shape -> shape.getBoundingBoxes().stream())
                .filter(shapeBox -> shapeBox.intersects(box));
    }

}
