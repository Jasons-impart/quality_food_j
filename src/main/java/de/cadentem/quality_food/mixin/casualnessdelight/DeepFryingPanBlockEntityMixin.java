package de.cadentem.quality_food.mixin.casualnessdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.tobitobi.casualnessdelight.common.block.entity.DeepFryingPanBlockEntity;

import java.util.List;

@Mixin(DeepFryingPanBlockEntity.class)
public abstract class DeepFryingPanBlockEntityMixin {
    @Final
    @Shadow(remap = false)
    private ItemStackHandler inventory;

    @ModifyArg(method = "cookAndOutputItems", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), index = 1, remap = false)
    private ItemStack quality_food$applyQuality(ItemStack stack, @Local(argsOnly = true) ItemStack cookingStack, @Local(argsOnly = true) net.minecraft.world.level.Level level) {
        QualityUtils.applyQuality(stack, List.of(inventory.getStackInSlot(0).isEmpty() ? cookingStack : inventory.getStackInSlot(0)), null, level.registryAccess());
        return stack;
    }
}
