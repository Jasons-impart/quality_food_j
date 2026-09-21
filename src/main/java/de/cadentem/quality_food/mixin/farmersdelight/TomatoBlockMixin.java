package de.cadentem.quality_food.mixin.farmersdelight;

import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

/** Provide harvest context (player + farmland) when tomatoes are picked by hand */
@Mixin(TomatoBlock.class)
public abstract class TomatoBlockMixin {
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/TomatoBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void quality_food$setDropData(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<net.minecraft.world.InteractionResult> callback) {
        BlockPos farmlandPos = pos.below();
        while (level.getBlockState(farmlandPos).is(ModBlocks.TOMATO_CROP.get())) {
            farmlandPos = farmlandPos.below();
        }
        DropData.CURRENT.set(DropData.create(LevelData.get(level, pos, true), state, player, level.getBlockState(farmlandPos), level, pos));
    }

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/TomatoBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    private void quality_food$clearDropData(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<net.minecraft.world.InteractionResult> callback) {
        DropData.CURRENT.remove();
    }
}
