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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "dev.xkmc.fruitsdelight.content.block.DoubleFruitBushBlock")
public abstract class DoubleFruitBushBlockMixin {
    @WrapMethod(method = "useWithoutItem")
    private InteractionResult quality_food$withHarvestContext(final BlockState state, final Level level, final BlockPos position, final Player player, final BlockHitResult hitResult, final Operation<InteractionResult> original) {
        // The target resolves the upper half to the lower half before harvesting - mirror that so quality and farmland are read from the base
        BlockPos base = position;
        BlockState baseState = state;

        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            base = position.below();
            baseState = level.getBlockState(base);
        }

        DropData previous = DropData.push(new DropData(LevelData.get(level, base, true), baseState, player, level.getBlockState(base.below()), level, base.immutable()));
        try {
            return original.call(state, level, position, player, hitResult);
        } finally {
            DropData.pop(previous);
        }
    }
}
