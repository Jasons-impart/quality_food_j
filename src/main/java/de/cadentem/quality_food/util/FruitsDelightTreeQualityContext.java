package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public final class FruitsDelightTreeQualityContext {
    private static final String FRUITS_DELIGHT = "fruitsdelight";
    private static final Set<String> CDPR_SAPLINGS = Set.of("jujube_sapling", "walnut_sapling");
    private static final ThreadLocal<Quality> SAPLING_QUALITY = new ThreadLocal<>();
    private static final ThreadLocal<Set<Long>> CHANGED_BLOCKS = new ThreadLocal<>();

    private FruitsDelightTreeQualityContext() {
    }

    public static void begin(final ServerLevel level, final BlockPos position, final BlockState state) {
        clear();
        if (!isSupportedSapling(state)) {
            return;
        }

        Quality quality = LevelData.get(level, position);
        if (!QualityUtils.isValidQuality(quality)) {
            return;
        }

        SAPLING_QUALITY.set(quality);
        CHANGED_BLOCKS.set(new HashSet<>());
    }

    public static void recordChangedBlock(final BlockPos position, final BlockState state) {
        Set<Long> changedBlocks = CHANGED_BLOCKS.get();
        if (changedBlocks != null && isTreeBlock(state)) {
            changedBlocks.add(position.asLong());
        }
    }

    public static void finish(final ServerLevel level, final boolean success) {
        Quality quality = SAPLING_QUALITY.get();
        Set<Long> changedBlocks = CHANGED_BLOCKS.get();

        try {
            if (!success || quality == null || changedBlocks == null) {
                return;
            }

            for (long packedPosition : changedBlocks) {
                BlockPos position = BlockPos.of(packedPosition);
                BlockState state = level.getBlockState(position);
                if (isTreeBlock(state) && Utils.isValidBlock(state.getBlock())) {
                    LevelData.set(level, position, quality);
                }
            }
        } finally {
            clear();
        }
    }

    private static boolean isSupportedSapling(final BlockState state) {
        if (!state.is(BlockTags.SAPLINGS)) {
            return false;
        }

        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return FRUITS_DELIGHT.equals(id.getNamespace())
                || "createdelightcore".equals(id.getNamespace()) && CDPR_SAPLINGS.contains(id.getPath());
    }

    private static boolean isTreeBlock(final BlockState state) {
        if (state.isAir()) {
            return false;
        }
        if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
            return true;
        }
        return FRUITS_DELIGHT.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace());
    }

    private static void clear() {
        SAPLING_QUALITY.remove();
        CHANGED_BLOCKS.remove();
    }
}
