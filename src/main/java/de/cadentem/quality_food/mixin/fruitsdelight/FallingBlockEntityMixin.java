package de.cadentem.quality_food.mixin.fruitsdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {
    @Unique
    private static final String quality_food$QUALITY_TAG = "quality_food:quality";

    @Unique
    private Quality quality_food$storedQuality = Quality.NONE;

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/level/block/state/BlockState;)V", at = @At("RETURN"))
    private void quality_food$captureDurianQuality(final Level level, final double x, final double y, final double z, final BlockState state, final CallbackInfo callback) {
        if (quality_food$isDurian(state)) {
            quality_food$storedQuality = LevelData.get(level, BlockPos.containing(x, y, z));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void quality_food$saveDurianQuality(final CompoundTag tag, final CallbackInfo callback) {
        if (!QualityUtils.isValidQuality(quality_food$storedQuality)) {
            return;
        }
        Quality.CODEC.encodeStart(NbtOps.INSTANCE, quality_food$storedQuality)
                .result()
                .ifPresent(encoded -> tag.put(quality_food$QUALITY_TAG, encoded));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void quality_food$loadDurianQuality(final CompoundTag tag, final CallbackInfo callback) {
        Tag encoded = tag.get(quality_food$QUALITY_TAG);
        if (encoded != null) {
            quality_food$storedQuality = Quality.CODEC.parse(NbtOps.INSTANCE, encoded)
                    .result()
                    .orElse(Quality.NONE);
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean quality_food$propagateDurianQuality(final Level level, final BlockPos position, final BlockState state, final int flags, final Operation<Boolean> original) {
        boolean placed = original.call(level, position, state, flags);
        if (placed && quality_food$isDurian(state)) {
            if (QualityUtils.isValidQuality(quality_food$storedQuality)) {
                LevelData.set(level, position, quality_food$storedQuality);
            }
        }
        return placed;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity quality_food$applyDurianItemQuality(final FallingBlockEntity self, final ItemLike item, final Operation<ItemEntity> original) {
        if (!quality_food$isDurian(self.getBlockState())) {
            return original.call(self, item);
        }

        ItemStack stack = new ItemStack(item);
        if (QualityUtils.isValidQuality(quality_food$storedQuality)) {
            QualityUtils.applyQuality(stack, quality_food$storedQuality);
        }
        return self.spawnAtLocation(stack);
    }

    @Unique
    private static boolean quality_food$isDurian(final BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().equals("fruitsdelight:durian");
    }
}
