package de.cadentem.quality_food.mixin.fruitsdelight;

import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.xkmc.fruitsdelight.content.block.DoubleBushBlock")
public abstract class DoubleBushBlockMixin {
    @Inject(method = "setGrowth", at = @At("RETURN"))
    private void quality_food$synchronizeUpperQuality(final Level level, final BlockPos position, final int age, final int flags, final CallbackInfo callback) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState upperState = level.getBlockState(position.above());
        if (!upperState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                || upperState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.UPPER) {
            return;
        }

        Quality quality = LevelData.get(serverLevel, position);
        if (QualityUtils.isValidQuality(quality)) {
            LevelData.set(serverLevel, position.above(), quality);
        }
    }
}
