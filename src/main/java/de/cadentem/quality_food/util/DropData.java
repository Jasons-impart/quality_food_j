package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DropData(Quality quality, BlockState state, Player player, BlockState farmland, Level level, BlockPos position) {
    public static final ThreadLocal<DropData> CURRENT = new ThreadLocal<>();
    public static final DropData SKIP = new DropData(Quality.NONE, null, null, null, null, null);

    public DropData(final Quality quality, final BlockState state, final Player player, final BlockState farmland) {
        this(quality, state, player, farmland, null, null);
    }

    public static DropData create(@NotNull final Quality quality, @Nullable final BlockState state, @Nullable final Entity entity, @Nullable final BlockState farmland) {
        return new DropData(quality, state, entity instanceof Player player ? player : null, farmland);
    }

    public static DropData create(@NotNull final Quality quality, @Nullable final BlockState state, @Nullable final Entity entity, @Nullable final BlockState farmland, @Nullable final Level level, @Nullable final BlockPos position) {
        return new DropData(quality, state, entity instanceof Player player ? player : null, farmland, level, position == null ? null : position.immutable());
    }

    public static @Nullable DropData push(final DropData data) {
        DropData previous = CURRENT.get();
        CURRENT.set(data);
        return previous;
    }

    public static void pop(@Nullable final DropData previous) {
        if (previous == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(previous);
        }
    }

    /**
     * Creates a harvest context for a manual (player) harvest of the crop at the given position <br>
     * For multi-block crops (same block stacked vertically) the quality is read from the harvested segment
     * while farmland and position are resolved from the lowest segment
     */
    public static DropData harvest(final Level level, final BlockPos position, final BlockState state, @Nullable final Player player) {
        BlockPos base = position;

        while (level.getBlockState(base.below()).is(state.getBlock())) {
            base = base.below();
        }

        return new DropData(LevelData.get(level, position, true), state, player, level.getBlockState(base.below()), level, base.immutable());
    }

    /** Runs the operation with the given context set and restores the previous context afterward (also on exceptions) */
    public static void runWith(final DropData data, final Runnable operation) {
        DropData previous = push(data);

        try {
            operation.run();
        } finally {
            pop(previous);
        }
    }
}
