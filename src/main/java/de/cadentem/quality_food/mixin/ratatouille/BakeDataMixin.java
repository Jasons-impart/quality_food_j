package de.cadentem.quality_food.mixin.ratatouille;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(targets = "org.forsteri.ratatouille.content.oven.BakeData")
public abstract class BakeDataMixin {
    /** The receiver is {@code OvenBlockEntity$Inventory} (extends ItemStackHandler) - coerced to avoid a compile dependency */
    @WrapOperation(method = "processFood", at = @At(value = "INVOKE", target = "Lorg/forsteri/ratatouille/content/oven/OvenBlockEntity$Inventory;setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V"))
    private void quality_food$applyBakedQuality(@Coerce final Object inventory, final int slot, final ItemStack result, final Operation<Void> original) {
        if (inventory instanceof IItemHandler handler) {
            Quality quality = QualityUtils.getQuality(handler.getStackInSlot(0));
            if (QualityUtils.isValidQuality(quality)) {
                QualityUtils.applyQuality(result, quality);
            }
        }

        original.call(inventory, slot, result);
    }
}
