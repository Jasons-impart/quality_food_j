package de.cadentem.quality_food.mixin.l2harvester;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.xkmc.l2harvester.compat.HarvesterController")
public abstract class HarvesterControllerMixin {
    @WrapOperation(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/api/behaviour/movement/MovementBehaviour;dropItem(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void quality_food$applyHarvestQuality(final MovementBehaviour behaviour, final MovementContext context, final ItemStack stack, final Operation<Void> original, @Local final BlockState state, @Local(argsOnly = true) final BlockPos position) {
        Level level = context.world;
        QualityUtils.applyQuality(stack, state, LevelData.get(level, position, true), null, level.getBlockState(position.below()), level.registryAccess(), level, position);
        original.call(behaviour, context, stack);
    }
}
