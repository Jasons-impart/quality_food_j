package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Remove stored quality if the quality block is removed */
@Mixin(Level.class)
public abstract class LevelMixin {
    @ModifyVariable(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I", shift = At.Shift.AFTER), argsOnly = true)
    private BlockState quality_food$removeQuality(final BlockState newState, @Local(argsOnly = true) final BlockPos position, @Local(ordinal = 1) final BlockState oldState) {
        Level level = (Level) (Object) this;

        if (level.isClientSide()) {
            return newState;
        }

        // Preserve quality across the vanilla growth variants that represent the same plant.
        // Every other block replacement must clear both stored quality and stale pending removals.
        if (!newState.is(oldState.getBlock()) && !quality_food$isStemTransition(oldState.getBlock(), newState.getBlock())) {
            level.getData(AttachmentHandler.LEVEL_DATA).remove(position, level.getGameTime());
        }

        return newState;
    }

    private static boolean quality_food$isStemTransition(final Block oldBlock, final Block newBlock) {
        return oldBlock == Blocks.MELON_STEM && newBlock == Blocks.ATTACHED_MELON_STEM
                || oldBlock == Blocks.ATTACHED_MELON_STEM && newBlock == Blocks.MELON_STEM
                || oldBlock == Blocks.PUMPKIN_STEM && newBlock == Blocks.ATTACHED_PUMPKIN_STEM
                || oldBlock == Blocks.ATTACHED_PUMPKIN_STEM && newBlock == Blocks.PUMPKIN_STEM
                || oldBlock == Blocks.CAVE_VINES && newBlock == Blocks.CAVE_VINES_PLANT
                || oldBlock == Blocks.CAVE_VINES_PLANT && newBlock == Blocks.CAVE_VINES;
    }
}
