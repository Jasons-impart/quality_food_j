package de.cadentem.quality_food.mixin.displaydelight;

import com.jkvin114.displaydelight.events.InterationManager;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InterationManager.class)
public abstract class InterationManagerMixin {
    @Inject(method = "tryTakeItemWithBareHand", at = @At("HEAD"), remap = false)
    private static void quality_food$storeQuality(Player player, ServerLevel world, BlockHitResult rez, CallbackInfoReturnable<Boolean> callback, @Share("bareHandQuality") LocalRef<Quality> quality) {
        quality.set(LevelData.get(world, rez.getBlockPos()));
    }

    @ModifyArg(method = "tryTakeItemWithBareHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static ItemStack quality_food$applyQualityToInventoryItem(ItemStack stack, @Local(argsOnly = true) ServerLevel world, @Local(argsOnly = true) BlockHitResult rez, @Share("bareHandQuality") LocalRef<Quality> quality) {
        QualityUtils.applyQuality(stack, quality.get());
        return stack;
    }

    @ModifyArg(method = "tryTakeItemWithBareHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"), index = 1)
    private static ItemStack quality_food$applyQualityToHandItem(ItemStack stack, @Local(argsOnly = true) ServerLevel world, @Local(argsOnly = true) BlockHitResult rez, @Share("bareHandQuality") LocalRef<Quality> quality) {
        QualityUtils.applyQuality(stack, quality.get());
        return stack;
    }

    /** Reject stacking items whose quality does not match the plated food */
    @Inject(method = "tryPlaceItemOnPlate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", shift = At.Shift.AFTER), cancellable = true)
    private static void quality_food$checkQuality(Player player, ServerLevel world, BlockHitResult rez, boolean isMainHand, CallbackInfoReturnable<Boolean> callback, @Local ItemStack handStack) {
        Quality blockQuality = LevelData.get(world, rez.getBlockPos());
        Quality itemQuality = QualityUtils.getQuality(handStack);
        if (!(blockQuality.equals(itemQuality) || (Quality.PLAYER_PLACED.equals(blockQuality) && Quality.NONE.equals(itemQuality)))) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = {"tryPlaceItemOnPlate", "tryPlaceItemOnSmallPlate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.AFTER))
    private static void quality_food$applyQuality(Player player, ServerLevel world, BlockHitResult rez, boolean isMainHand, CallbackInfoReturnable<Boolean> callback, @Local ItemStack handStack) {
        Quality quality = QualityUtils.getQuality(handStack);
        LevelData.set(world, rez.getBlockPos(), !Quality.NONE.equals(quality) ? quality : Quality.PLAYER_PLACED);
    }

    @Inject(method = "tryPlaceItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;swing(Lnet/minecraft/world/InteractionHand;Z)V"))
    private static void quality_food$applyQualityOnDisplay(Player player, ServerLevel world, BlockHitResult rez, boolean isMainHand, CallbackInfoReturnable<Boolean> callback, @Local ItemStack stack) {
        Quality quality = QualityUtils.getQuality(stack);
        LevelData.set(world, rez.getBlockPos(), !Quality.NONE.equals(quality) ? quality : Quality.PLAYER_PLACED);
    }
}
