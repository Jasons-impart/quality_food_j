package de.cadentem.quality_food.mixin.rightclickharvest;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.DropData;
import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/** Roll quality with more context */
@Mixin(value = RightClickHarvest.class, remap = false)
public abstract class RightClickHarvestMixin {
    @WrapMethod(method = "dropStacks")
    private static void quality_food$withDropData(final BlockState state, final ServerLevel level, final BlockPos position, final Entity entity, final ItemStack tool, boolean removePlant, final Operation<Void> original) {
        DropData data = DropData.create(LevelData.get(level, position, true), state, entity, level.getBlockState(position.below()), level, position);
        DropData.runWith(data, () -> original.call(state, level, position, entity, tool, removePlant));
    }
}
