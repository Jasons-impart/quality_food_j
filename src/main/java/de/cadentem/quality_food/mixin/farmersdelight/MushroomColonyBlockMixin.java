package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

/** Provide harvest context (player + ground) when mushroom colonies are harvested with shears */
@Mixin(MushroomColonyBlock.class)
public abstract class MushroomColonyBlockMixin {
    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/MushroomColonyBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void quality_food$withHarvestContext(final Level level, final BlockPos position, final ItemStack stack, final Operation<Void> original, @Local(argsOnly = true) final BlockState state, @Local(argsOnly = true) final Player player) {
        DropData.runWith(DropData.harvest(level, position, state, player), () -> original.call(level, position, stack));
    }
}
