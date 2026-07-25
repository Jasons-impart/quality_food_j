package de.cadentem.quality_food.mixin.ratatouille;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "org.forsteri.ratatouille.content.oven.BakeData")
public abstract class BakeDataMixin {
    @ModifyArg(method = "processFood", index = 1, at = @At(value = "INVOKE", target = "Lorg/forsteri/ratatouille/content/oven/OvenBlockEntity$Inventory;setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack quality_food$applyBakedQuality(final ItemStack result, @Local final IItemHandler inventory) {
        Quality quality = QualityUtils.getQuality(inventory.getStackInSlot(0));
        if (QualityUtils.isValidQuality(quality)) {
            QualityUtils.applyQuality(result, quality);
        }
        return result;
    }
}
