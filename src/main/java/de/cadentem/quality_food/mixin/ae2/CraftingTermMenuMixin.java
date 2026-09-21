package de.cadentem.quality_food.mixin.ae2;

import appeng.menu.me.items.CraftingTermMenu;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.compat.SpecialContainer;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Apply quality when the terminal shows a conversion result. */
@Mixin(value = CraftingTermMenu.class, remap = false)
public abstract class CraftingTermMenuMixin {
    @Shadow private RecipeHolder<CraftingRecipe> currentRecipe;

    @ModifyArg(method = "updateCurrentRecipeAndOutput", at = @At(value = "INVOKE", target = "Lappeng/menu/slot/CraftingTermSlot;set(Lnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack quality_food$handleConversion(final ItemStack result, @Local final Level level, @Local final CraftingInput testInput) {
        if (result.isEmpty() || this.currentRecipe == null) {
            return result;
        }

        SpecialContainer container = new SpecialContainer(testInput.size());

        for (int index = 0; index < testInput.size(); index++) {
            container.setItem(index, testInput.getItem(index));
        }

        QualityUtils.handleConversion(result, container, currentRecipe, level.registryAccess());
        return result;
    }
}
