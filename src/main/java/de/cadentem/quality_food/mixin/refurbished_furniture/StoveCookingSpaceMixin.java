package de.cadentem.quality_food.mixin.refurbished_furniture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.mrcrayfish.furniture.refurbished.blockentity.StoveBlockEntity$CookingSpace")
public abstract class StoveCookingSpaceMixin {
    @WrapOperation(method = "onCompleteProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack quality_food$applyCookedQuality(final ItemStack recipeResult, final Operation<ItemStack> original, @Local(ordinal = 0) final ItemStack input) {
        ItemStack result = original.call(recipeResult);
        quality_food$copyQuality(input, result);
        return result;
    }

    @ModifyArg(method = "canProcess", at = @At(value = "INVOKE", target = "Lcom/mrcrayfish/furniture/refurbished/blockentity/StoveBlockEntity$CookingSpace;canOutput(Lnet/minecraft/world/item/ItemStack;)Z"))
    private ItemStack quality_food$checkQualityAwareOutput(final ItemStack recipeResult, @Local(ordinal = 0) final ItemStack input) {
        ItemStack result = recipeResult.copy();
        quality_food$copyQuality(input, result);
        return result;
    }

    private static void quality_food$copyQuality(final ItemStack input, final ItemStack result) {
        Quality quality = QualityUtils.getQuality(input);
        if (QualityUtils.isValidQuality(quality)) {
            QualityUtils.applyQuality(result, quality);
        }
    }
}
