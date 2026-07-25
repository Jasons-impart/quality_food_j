package de.cadentem.quality_food.mixin.ratatouille;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(targets = "org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlockEntity")
public abstract class SqueezeBasinBlockEntityMixin {
    @Unique
    private static final ThreadLocal<Quality> quality_food$inputQuality = new ThreadLocal<>();

    @WrapOperation(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void quality_food$captureInputQuality(final ItemStack input, final int amount, final Operation<Void> original) {
        quality_food$inputQuality.set(QualityUtils.getQuality(input));
        original.call(input, amount);
    }

    @ModifyArg(method = "process", index = 0, at = @At(value = "INVOKE", target = "Lorg/forsteri/ratatouille/content/squeeze_basin/SqueezeBasinBlockEntity;acceptOutputs(Ljava/util/List;Z)Z"))
    private List<ItemStack> quality_food$applyOutputQuality(final List<ItemStack> outputs) {
        Quality quality = quality_food$inputQuality.get();
        if (QualityUtils.isValidQuality(quality)) {
            outputs.forEach(stack -> QualityUtils.applyQuality(stack, quality));
        }
        return outputs;
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
