package de.cadentem.quality_food.mixin.mynethersdelight;

import com.soytutta.mynethersdelight.common.block.crops.PowderyCaneBlock;
import com.soytutta.mynethersdelight.common.registry.MNDBlocks;
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

@Mixin(PowderyCaneBlock.class)
public abstract class PowderyCaneBlockMixin {
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lcom/soytutta/mynethersdelight/common/block/crops/PowderyCaneBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void quality_food$setDropData(net.minecraft.world.item.ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<?> callback) {
        BlockPos farmlandPos = pos.below();
        while (level.getBlockState(farmlandPos).is(MNDBlocks.POWDERY_CANE.get())) {
            farmlandPos = farmlandPos.below();
        }
        DropData.CURRENT.set(DropData.create(LevelData.get(level, pos, true), state, player, level.getBlockState(farmlandPos), level, pos));
    }

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lcom/soytutta/mynethersdelight/common/block/crops/PowderyCaneBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    private void quality_food$clearDropData(net.minecraft.world.item.ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<?> callback) {
        DropData.CURRENT.remove();
    }
}
