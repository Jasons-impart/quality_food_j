package de.cadentem.quality_food.mixin.ftbultimine;

import de.cadentem.quality_food.util.DropData;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ftb.mods.ftbultimine.api.util.ItemCollector")
public abstract class ItemCollectorMixin {
    @Inject(method = "add", at = @At("HEAD"), remap = false)
    private void quality_food$applyQuality(final ItemStack stack, final CallbackInfo callback) {
        DropData data = DropData.CURRENT.get();
        if (data == null || data == DropData.SKIP || data.state() == null || !Utils.isValidItem(stack) || QualityUtils.hasQuality(stack)) {
            return;
        }

        Player player = data.player();
        if (player == null) {
            return;
        }

        QualityUtils.applyQuality(
                stack,
                data.state(),
                data.quality(),
                player,
                data.farmland(),
                player.level().registryAccess(),
                data.level(),
                data.position()
        );
    }
}
