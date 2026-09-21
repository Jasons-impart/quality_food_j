package de.cadentem.quality_food.mixin.neapolitan;

import com.teamabnormals.neapolitan.common.block.StrawberryBushBlock;
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

@Mixin(StrawberryBushBlock.class)
public abstract class StrawberryBushBlockMixin {
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/neapolitan/common/block/StrawberryBushBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void quality_food$setDropData(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<net.minecraft.world.InteractionResult> callback) {
        DropData.CURRENT.set(DropData.create(LevelData.get(level, pos, true), state, player, level.getBlockState(pos.below()), level, pos));
    }

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/neapolitan/common/block/StrawberryBushBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    private void quality_food$clearDropData(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.phys.BlockHitResult hit, CallbackInfoReturnable<net.minecraft.world.InteractionResult> callback) {
        DropData.CURRENT.remove();
    }
}
