package de.cadentem.quality_food.mixin.fruitsdelight;

import de.cadentem.quality_food.util.FruitsDelightTreeQualityContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    private void quality_food$recordTreeBlock(final BlockPos position, final BlockState state, final int flags, final int recursionLeft, final CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue()) {
            FruitsDelightTreeQualityContext.recordChangedBlock(position, state);
        }
    }
}
