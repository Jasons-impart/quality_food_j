package de.cadentem.quality_food.mixin.fruitsdelight;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.cadentem.quality_food.util.FruitsDelightTreeQualityContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TreeGrower.class)
public abstract class AbstractTreeGrowerMixin {
    @WrapMethod(method = "growTree")
    private boolean quality_food$propagateTreeQuality(final ServerLevel level, final ChunkGenerator generator, final BlockPos position, final BlockState state, final RandomSource random, final Operation<Boolean> original) {
        FruitsDelightTreeQualityContext.begin(level, position, state);
        boolean success = false;
        try {
            success = original.call(level, generator, position, state, random);
            return success;
        } finally {
            FruitsDelightTreeQualityContext.finish(level, success);
        }
    }
}
