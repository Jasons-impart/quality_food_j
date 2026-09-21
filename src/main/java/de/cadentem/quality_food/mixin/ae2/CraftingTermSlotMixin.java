package de.cadentem.quality_food.mixin.ae2;

import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.menu.slot.CraftingTermSlot;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.compat.SpecialContainer;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CraftingTermSlot.class, remap = false)
public abstract class CraftingTermSlotMixin {
    @ModifyVariable(method = "craftItem", at = @At(value = "INVOKE", target = "Lappeng/menu/slot/CraftingTermSlot;makeItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"), ordinal = 0, argsOnly = false, name = "is")
    private ItemStack quality_food$applyQuality(final ItemStack result, @Local(argsOnly = true) final Player player, @Local final Level level, @Local(ordinal = 0) final ItemStack[] extractedItems) {
        RecipeHolder<?> recipe = ((net.minecraft.world.inventory.RecipeCraftingHolder) this).getRecipeUsed();
        if (level.isClientSide() || !(recipe != null && recipe.value() instanceof CraftingRecipe) || result.isEmpty()
                || ServerConfig.isNoQualityRecipe(recipe, level.registryAccess())) {
            return result;
        }

        SpecialContainer container = new SpecialContainer(9);
        NonNullList<ItemStack> ingredients = NonNullList.withSize(9, ItemStack.EMPTY);

        for (int index = 0; index < extractedItems.length && index < ingredients.size(); index++) {
            ItemStack stack = extractedItems[index];
            ingredients.set(index, stack == null ? ItemStack.EMPTY : stack);
            container.setItem(index, ingredients.get(index));
        }

        QualityUtils.handleConversion(result, container, recipe, level.registryAccess());

        if (!QualityUtils.hasQuality(result)) {
            QualityUtils.applyQuality(result, ingredients, player, level.registryAccess());
        }

        return result;
    }
}
