package de.cadentem.quality_food.mixin.mynethersdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.soytutta.mynethersdelight.common.block.crops.PowderyCannonBlock;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Provide harvest context (player + farmland below the lowest segment) when powdery cannons are harvested by hand */
@Mixin(PowderyCannonBlock.class)
public abstract class PowderyCannonBlockMixin {
    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lcom/soytutta/mynethersdelight/common/block/crops/PowderyCannonBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void quality_food$withHarvestContext(final Level level, final BlockPos position, final ItemStack stack, final Operation<Void> original, @Local(argsOnly = true) final BlockState state, @Local(argsOnly = true) final Player player) {
        DropData.runWith(DropData.harvest(level, position, state, player), () -> original.call(level, position, stack));
    }
}
