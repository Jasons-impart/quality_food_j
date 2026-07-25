package de.cadentem.quality_food.mixin.ratatouille;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "org.forsteri.ratatouille.content.thresher.ThresherBlockEntity")
public abstract class ThresherBlockEntityMixin {
    @Unique
    private static final ThreadLocal<Quality> quality_food$inputQuality = new ThreadLocal<>();

    @WrapOperation(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void quality_food$captureInputQuality(final ItemStack input, final int amount, final Operation<Void> original) {
        quality_food$inputQuality.set(QualityUtils.getQuality(input));
        original.call(input, amount);
    }

    @WrapOperation(method = "lambda$process$1", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/items/ItemHandlerHelper;insertItemStacked(Lnet/neoforged/neoforge/items/IItemHandler;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack quality_food$applyOutputQuality(final IItemHandler inventory, final ItemStack output, final boolean simulate, final Operation<ItemStack> original) {
        Quality quality = quality_food$inputQuality.get();
        if (QualityUtils.isValidQuality(quality)) {
            QualityUtils.applyQuality(output, quality);
        }
        return original.call(inventory, output, simulate);
    }

    @WrapMethod(method = "process")
    private void quality_food$clearInputQuality(final Operation<Void> original) {
        try {
            original.call();
        } finally {
            quality_food$inputQuality.remove();
        }
    }
}
