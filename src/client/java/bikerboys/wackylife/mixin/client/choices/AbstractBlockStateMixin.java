package bikerboys.wackylife.mixin.client.choices;

import bikerboys.wackylife.choices.*;
import net.minecraft.block.*;
import net.minecraft.block.enums.*;
import net.minecraft.client.*;
import net.minecraft.fluid.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow public abstract FluidState getFluidState();

    @Shadow @Final private NoteBlockInstrument instrument;

    @Inject(at = @At("HEAD"),
            method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
            cancellable = true)
    private void onGetCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        if (getFluidState().isEmpty()) return;

        if (!ChoiceTicker.hasChoice("jesus", true)) return;

        if (client.options.sneakKey.isPressed()) return;

        Box playerBox = client.player.getBoundingBox();

        // block must be below the player
        if (pos.getY() >= playerBox.minY) return;

        Box range = playerBox.expand(3.0, 1.0, 3.0);

        if (!range.intersects(new Box(pos))) return;

        cir.setReturnValue(VoxelShapes.fullCube());
    }
}
