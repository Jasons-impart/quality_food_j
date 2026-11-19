package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.HarvestQualityRoll;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements HarvestQualityRoll {
    @Unique private boolean quality_food$harvestRolled;

    @Override
    public boolean quality_food$startHarvestRoll() {
        if (quality_food$harvestRolled) {
            return false;
        }

        quality_food$harvestRolled = true;
        return true;
    }
}
