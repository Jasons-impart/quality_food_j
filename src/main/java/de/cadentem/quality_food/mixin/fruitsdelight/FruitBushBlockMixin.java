package de.cadentem.quality_food.mixin.fruitsdelight;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.DropData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "dev.xkmc.fruitsdelight.content.block.FruitBushBlock")
public abstract class FruitBushBlockMixin {
    @WrapMethod(method = "useWithoutItem")
    private InteractionResult quality_food$withHarvestContext(final BlockState state, final Level level, final BlockPos position, final Player player, final BlockHitResult hitResult, final Operation<InteractionResult> original) {
        DropData previous = DropData.push(new DropData(LevelData.get(level, position, true), state, player, level.getBlockState(position.below()), level, position.immutable()));
        try {
            return original.call(state, level, position, player, hitResult);
        } finally {
            DropData.pop(previous);
        }
    }
}
