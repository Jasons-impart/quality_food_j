package de.cadentem.quality_food.util;

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
}
