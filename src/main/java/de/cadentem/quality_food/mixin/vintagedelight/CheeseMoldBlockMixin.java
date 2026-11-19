package de.cadentem.quality_food.mixin.vintagedelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"net.ribs.vintagedelight.block.CheeseMoldBlock", "net.ribs.vintagedelight.block.custom.CheeseMoldBlock"})
public abstract class CheeseMoldBlockMixin {
    @Shadow @Final private static IntegerProperty LEVEL;
    @Unique private static final ResourceLocation quality_food$cheeseCurds = Compat.location(Compat.Mod.VINTAGEDELIGHT.modid(), "cheese_curds");

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void quality_food$checkPlacement(final ItemStack stack, final BlockState state, final Level level, final BlockPos position, final Player player, final InteractionHand hand, final BlockHitResult hit, final CallbackInfoReturnable<ItemInteractionResult> callback) {
        if (level.isClientSide()) {
            callback.setReturnValue(ItemInteractionResult.sidedSuccess(true));
            return;
        }

        int size = state.getValue(LEVEL);
        if (size > 0 && size < 4 && quality_food$cheeseCurds.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()))
                && !LevelData.get(level, position).equals(QualityUtils.getQuality(stack))) {
            callback.setReturnValue(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        }
    }

    /** Initialize or remove quality when adding / removing entries from the stack */
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void quality_food$handleQuality(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos position, final Player player, final InteractionHand hand, final BlockHitResult hit, final CallbackInfoReturnable<ItemInteractionResult> callback) {
        LevelData.set(level, position, QualityUtils.getQuality(itemStack));
    }

    /** Apply quality to taken stacks */
    @ModifyVariable(method = "useWithoutItem", at = @At(value = "STORE"), name = "cheeseItem")
    private ItemStack quality_food$applyQuality(final ItemStack stack, @Local(argsOnly = true) final Level level, @Local(argsOnly = true) final BlockPos position) {
        Quality quality = LevelData.get(level, position, true);

        if (quality.level() > 0) {
            QualityUtils.applyQuality(stack, quality);
        }

        return stack;
    }

    /** Remove quality when empty */
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void quality_food$removeQuality(final BlockState state, final Level level, final BlockPos position, final Player player, final BlockHitResult hit, final CallbackInfoReturnable<InteractionResult> callback) {
        LevelData.remove(level, position);
    }
}
