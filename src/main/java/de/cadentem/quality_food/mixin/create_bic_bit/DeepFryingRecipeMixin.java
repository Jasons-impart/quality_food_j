package de.cadentem.quality_food.mixin.create_bic_bit;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.pyzpre.createbitterballen.block.mechanicalfryer.DeepFryingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DeepFryingRecipe.class)
public abstract class DeepFryingRecipeMixin {
    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), remap = false)
    private static void quality_food$storeInput(CallbackInfoReturnable<Boolean> callback, @Share("input") LocalRef<ItemStack> input, @Local IItemHandler availableItems) {
        input.set(availableItems.getStackInSlot(0).copy());
    }

    @ModifyArg(method = "apply", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/items/ItemHandlerHelper;insertItemStacked(Lnet/neoforged/neoforge/items/IItemHandler;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;"), index = 1, remap = false)
    private static ItemStack quality_food$applyQuality(ItemStack stack, @Share("input") LocalRef<ItemStack> input, @Local BasinBlockEntity basin) {
        ItemStack inputStack = input.get();
        if (inputStack == null || basin.getLevel() == null) {
            return stack;
        }
        QualityUtils.applyQuality(stack, List.of(inputStack), null, basin.getLevel().registryAccess());
        return stack;
    }
}
