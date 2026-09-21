package de.cadentem.quality_food.mixin.trailandtales_delight;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import show.tatd.mod.block.BuddingLanternFruitBlock;

/** Preserve the stored quality when the budding lantern fruit converts into the grown crop */
@Mixin(BuddingLanternFruitBlock.class)
public abstract class BuddingLanternFruitBlockMixin {
    @Inject(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void quality_food$captureQualityOnUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> callback, @Share("quality_food$lanternFruitQualityOnUpdateShape") LocalRef<Quality> qualityRef) {
        qualityRef.set(LevelData.get(level, pos));
    }

    @Inject(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.AFTER))
    private void quality_food$restoreQualityOnUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> callback, @Share("quality_food$lanternFruitQualityOnUpdateShape") LocalRef<Quality> qualityRef) {
        quality_food$restoreQuality(level, pos, qualityRef.get());
    }

    @Inject(method = "growPastMaxAge", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private void quality_food$captureQualityOnGrowPastMaxAge(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo callback, @Share("quality_food$lanternFruitQualityOnGrowPastMaxAge") LocalRef<Quality> qualityRef) {
        qualityRef.set(LevelData.get(level, pos));
    }

    @Inject(method = "growPastMaxAge", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", shift = At.Shift.AFTER))
    private void quality_food$restoreQualityOnGrowPastMaxAge(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo callback, @Share("quality_food$lanternFruitQualityOnGrowPastMaxAge") LocalRef<Quality> qualityRef) {
        quality_food$restoreQuality(level, pos, qualityRef.get());
    }

    @Inject(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1))
    private void quality_food$captureQualityOnBonemealTransition(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo callback, @Share("quality_food$lanternFruitQualityOnBonemealTransition") LocalRef<Quality> qualityRef) {
        qualityRef.set(LevelData.get(level, pos));
    }

    @Inject(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1, shift = At.Shift.AFTER))
    private void quality_food$restoreQualityOnBonemealTransition(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo callback, @Share("quality_food$lanternFruitQualityOnBonemealTransition") LocalRef<Quality> qualityRef) {
        quality_food$restoreQuality(level, pos, qualityRef.get());
    }

    @Unique
    private static void quality_food$restoreQuality(LevelAccessor level, BlockPos pos, Quality quality) {
        if (quality != null) {
            LevelData.set(level, pos, quality);
        }
    }
}
