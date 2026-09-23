package de.cadentem.quality_food.mixin.ae2;

import appeng.api.inventories.InternalInventory;
import appeng.menu.slot.CraftingTermSlot;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.compat.SpecialContainer;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CraftingTermSlot.class, remap = false)
public abstract class CraftingTermSlotMixin {
    @Shadow abstract InternalInventory getPattern();

    /**
     * Runs right before {@code makeItem} - at this point the crafting grid still holds the items which are
     * about to be consumed (the extracted {@code set} only contains the replacements pulled from the network)
     */
    @ModifyVariable(method = "craftItem", at = @At(value = "INVOKE", target = "Lappeng/menu/slot/CraftingTermSlot;makeItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"), ordinal = 0, argsOnly = false, name = "is")
    private ItemStack quality_food$applyQuality(final ItemStack result, @Local(argsOnly = true) final Player player, @Local final Level level) {
        RecipeHolder<?> recipe = ((RecipeCraftingHolder) this).getRecipeUsed();
        if (level.isClientSide() || !(recipe != null && recipe.value() instanceof CraftingRecipe) || result.isEmpty()
                || ServerConfig.isNoQualityRecipe(recipe, level.registryAccess())) {
            return result;
        }

        InternalInventory pattern = getPattern();
        int size = Math.min(9, pattern.size());
        SpecialContainer container = new SpecialContainer(size);
        NonNullList<ItemStack> ingredients = NonNullList.withSize(size, ItemStack.EMPTY);

        for (int index = 0; index < size; index++) {
            ItemStack stack = pattern.getStackInSlot(index);
            // One item per slot is consumed per craft - the grid may hold more
            ItemStack consumed = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
            ingredients.set(index, consumed);
            container.setItem(index, consumed);
        }

        QualityUtils.handleConversion(result, container, recipe, level.registryAccess());

        if (!QualityUtils.hasQuality(result)) {
            QualityUtils.applyQuality(result, ingredients, player, level.registryAccess());
        }

        return result;
    }
}
