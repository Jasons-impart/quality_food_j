package de.cadentem.quality_food.mixin.functionalstorage;

import com.buuz135.functionalstorage.util.CompactingUtil;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** Preserve quality through compacting-drawer tier resolution */
@Mixin(CompactingUtil.class)
public abstract class CompactingUtilMixin {
    @Inject(method = "findAllMatchingRecipes", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = false)
    private void quality_food$applyQualityToResult(CraftingInput crafting, CallbackInfoReturnable<List<ItemStack>> callback, @Local ItemStack result) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(crafting.getItem(0)));
    }

    @ModifyArg(method = "findUpperTier", at = @At(value = "INVOKE", target = "Lcom/buuz135/functionalstorage/util/CompactingUtil$Result;<init>(Lnet/minecraft/world/item/ItemStack;I)V"), index = 0, remap = false)
    private ItemStack quality_food$applyQualityUpper(ItemStack result, @Local(argsOnly = true) ItemStack stack) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(stack));
        return result;
    }

    @ModifyArg(method = "findLowerTier", at = @At(value = "INVOKE", target = "Lcom/buuz135/functionalstorage/util/CompactingUtil$Result;<init>(Lnet/minecraft/world/item/ItemStack;I)V"), index = 0, remap = false)
    private ItemStack quality_food$applyQualityLower(ItemStack result, @Local(argsOnly = true) ItemStack stack) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(stack));
        return result;
    }
}
