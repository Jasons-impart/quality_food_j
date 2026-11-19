package de.cadentem.quality_food.core.loot_modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.DropData;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class QualityLootModifier extends LootModifier {
    public static final String ID = "quality_loot_modifier";
    public static final MapCodec<QualityLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> LootModifier.codecStart(instance).apply(instance, QualityLootModifier::new));

    private static final ResourceLocation FISHING_LOOT_TABLE = ResourceLocation.withDefaultNamespace("gameplay/fishing");

    public QualityLootModifier(final LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(final ObjectArrayList<ItemStack> generatedLoot, @NotNull final LootContext context) {
        if (generatedLoot.isEmpty()) {
            return generatedLoot;
        }

        ResourceLocation lootTable = context.getQueriedLootTableId();

        if (/* Handled by NeoForge events (LivingDropsEvent / ItemFishedEvent) */ lootTable.getPath().startsWith("entities") || lootTable.equals(FISHING_LOOT_TABLE)) {
            return generatedLoot;
        }

        Player playerReference = context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Player player ? player : null;
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);

        if (state != null && origin != null) {
            BlockPos position = BlockPos.containing(origin);
            DropData data = DropData.CURRENT.get();
            if (data == DropData.SKIP) {
                return generatedLoot;
            }
            if (data != null && data.level() == context.getLevel() && position.equals(data.position())) {
                generatedLoot.forEach(stack -> QualityUtils.applyHarvestQuality(
                        stack, state, data.quality(), data.player() != null ? data.player() : playerReference,
                        data.farmland(), context.getLevel().registryAccess(), context.getLevel(), position));
            } else {
                BlockState farmland = context.getLevel().getBlockState(position.below());
                Quality quality = LevelData.get(context.getLevel(), position, true);
                generatedLoot.forEach(stack -> QualityUtils.applyHarvestQuality(
                        stack, state, quality, playerReference, farmland, context.getLevel().registryAccess(), context.getLevel(), position));
            }
        } else {
            generatedLoot.stream().filter(Utils::isValidItem).forEach(stack -> QualityUtils.applyQuality(
                    stack, playerReference, ServerConfig.MAX_NATURAL_LOOT_QUALITY_LEVEL.get(), context.getLevel().registryAccess()));
        }

        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
