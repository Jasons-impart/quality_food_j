package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

@Mixin(MushroomColonyBlock.class)
public abstract class MushroomColonyBlockMixin {
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/MushroomColonyBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void quality_food$setDropData(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<?> callback) {
        DropData.CURRENT.set(DropData.create(LevelData.get(level, pos, true), state, player, level.getBlockState(pos.below()), level, pos));
    }

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/MushroomColonyBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    private void quality_food$clearDropData(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<?> callback) {
        DropData.CURRENT.remove();
    }
}
