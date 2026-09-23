package de.cadentem.quality_food.mixin.ftbultimine;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

/**
 * Right-click harvesting of FTB Ultimine collects all drops first and pops them at the end - <br>
 * the harvest context is therefore applied while collecting (see {@link ItemCollectorMixin}) and skipped while popping
 */
@Mixin(targets = "dev.ftb.mods.ftbultimine.rightclick.CropHarvesting")
public abstract class CropHarvestingMixin {
    @WrapOperation(method = "lambda$handleRightClickBlock$0", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbultimine/api/crop/CropLikeHandler;doHarvesting(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ldev/ftb/mods/ftbultimine/api/util/ItemCollector;)Z"), remap = false)
    private static boolean quality_food$withHarvestContext(@Coerce final Object handler, final Player player, final BlockPos position, final BlockState state, @Coerce final Object collector, final Operation<Boolean> original) {
        DropData previous = DropData.push(DropData.harvest(player.level(), position, state, player));

        try {
            return original.call(handler, player, position, state, collector);
        } finally {
            DropData.pop(previous);
        }
    }

    @WrapOperation(method = "handleRightClickBlock", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbultimine/api/util/ItemCollector;drop(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), remap = false)
    private static void quality_food$skipGenericRoll(@Coerce final Object collector, final Level level, final BlockPos position, final Operation<Void> original) {
        DropData.runWith(DropData.SKIP, () -> original.call(collector, level, position));
    }
}
