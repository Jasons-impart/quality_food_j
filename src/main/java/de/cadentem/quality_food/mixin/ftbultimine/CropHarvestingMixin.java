package de.cadentem.quality_food.mixin.ftbultimine;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ftb.mods.ftbultimine.rightclick.CropHarvesting")
public abstract class CropHarvestingMixin {
    @Inject(method = "lambda$handleRightClickBlock$0", at = @At("HEAD"), remap = false)
    private static void quality_food$pushDropData(
            final CallbackInfo callback,
            @Local(argsOnly = true) final ServerPlayer player,
            @Local(argsOnly = true) final BlockPos position,
            @Local(argsOnly = true) final BlockState state,
            @Share("quality_food$previousDropData") final LocalRef<DropData> previousRef
    ) {
        DropData data = DropData.create(
                LevelData.get(player.level(), position, true),
                state,
                player,
                player.level().getBlockState(position.below()),
                player.level(),
                position
        );
        previousRef.set(DropData.push(data));
    }

    @Inject(method = "lambda$handleRightClickBlock$0", at = @At("RETURN"), remap = false)
    private static void quality_food$popDropData(
            final CallbackInfo callback,
            @Share("quality_food$previousDropData") final LocalRef<DropData> previousRef
    ) {
        DropData.pop(previousRef.get());
    }
}
