package de.cadentem.quality_food.mixin.refurbished_furniture;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.mrcrayfish.furniture.refurbished.blockentity.FryingPanBlockEntity")
public abstract class FryingPanBlockEntityMixin {
    @ModifyArg(method = "onCompleteCooking", index = 1, at = @At(value = "INVOKE", target = "Lcom/mrcrayfish/furniture/refurbished/blockentity/FryingPanBlockEntity;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack quality_food$applyCookedQuality(final ItemStack result, @Local(ordinal = 0) final ItemStack input) {
        Quality quality = QualityUtils.getQuality(input);
        if (QualityUtils.isValidQuality(quality)) {
            QualityUtils.applyQuality(result, quality);
        }
        return result;
    }
}
