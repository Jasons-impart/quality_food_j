package de.cadentem.quality_food.mixin.farm_and_charm;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClimbingCropBlock.class)
public abstract class ClimbingCropBlockMixin {
    @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/satisfy/farm_and_charm/core/block/crops/ClimbingCropBlock;dropFruits(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void quality_food$withDropData(final ClimbingCropBlock crop, final Level level, final BlockPos position, final BlockState state, final Operation<Void> original, @Local(argsOnly = true) final Player player) {
        DropData.runWith(DropData.harvest(level, position, state, player), () -> original.call(crop, level, position, state));
    }
}
