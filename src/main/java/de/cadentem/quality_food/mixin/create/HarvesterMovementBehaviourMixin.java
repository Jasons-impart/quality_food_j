package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HarvesterMovementBehaviour.class, remap = false)
public abstract class HarvesterMovementBehaviourMixin {
    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;destroyBlockAs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;FLjava/util/function/Consumer;)V", shift = At.Shift.BEFORE))
    private void quality_food$storeData(final MovementContext context, final BlockPos position, final CallbackInfo callback, @Share("cropQuality") final LocalRef<Quality> quality) {
        quality.set(LevelData.get(context.world, position));
    }

    /** Restore the harvested crop's quality after Create successfully replants it. */
    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", shift = At.Shift.AFTER, remap = true))
    private void quality_food$retainQuality(final MovementContext context, final BlockPos position, final CallbackInfo callback, @Local(name = "cutCrop") final BlockState cutCrop, @Local(name = "seedSubtracted") final MutableBoolean seedSubtracted, @Share("cropQuality") final LocalRef<Quality> quality) {
        if (seedSubtracted.booleanValue() && !cutCrop.isAir() && context.world.getBlockState(position).is(cutCrop.getBlock())) {
            LevelData.set(context.world, position, quality.get());
        }
    }
}
