package de.cadentem.quality_food.core.attachments;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class LevelData implements INBTSerializable<CompoundTag> {
    private final Map<Long, Quality> qualities = new ConcurrentHashMap<>();

    /** Qualities of blocks removed within their own game tick, consumable once via {@link #get(LevelAccessor, BlockPos, boolean)} */
    private final Map<Long, RemovedQuality> pendingRemovals = new ConcurrentHashMap<>();

    private long lastSweepGameTime = -1;

    public @NotNull Quality get(final BlockPos position) {
        Quality quality = qualities.get(position.asLong());

        if (quality == null) {
            return Quality.NONE;
        }

        return quality;
    }

    /** Passing {@code null} or {@link Quality#NONE} removes the entry */
    public synchronized void set(final BlockPos position, final @Nullable Quality quality) {
        if (quality == null || Quality.NONE.equals(quality)) {
            qualities.remove(position.asLong());
            return;
        }

        qualities.put(position.asLong(), quality);
    }

    public synchronized void remove(final BlockPos position, final long gameTime) {
        long key = position.asLong();
        Quality removed = qualities.remove(key);

        // Records from older ticks can never be consumed again - sweep at most once per tick
        if (lastSweepGameTime != gameTime) {
            pendingRemovals.values().removeIf(record -> record.gameTime() != gameTime);
            lastSweepGameTime = gameTime;
        }

        // A different block may now occupy this position - an unconsumed pending record
        // would leak the previous block's quality to its drops
        pendingRemovals.remove(key);

        if (removed != null) {
            pendingRemovals.put(key, new RemovedQuality(removed, gameTime));
        }
    }

    @Override
    public synchronized @NotNull CompoundTag serializeNBT(final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        for (Map.Entry<Long, Quality> entry : qualities.entrySet()) {
            Optional<Tag> qualityTag = Quality.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).resultOrPartial(QualityFood.LOG::error);
            qualityTag.ifPresent(value -> tag.put(String.valueOf(entry.getKey()), value));
        }

        return tag;
    }

    @Override
    public synchronized void deserializeNBT(final HolderLookup.Provider provider, final CompoundTag tag) {
        qualities.clear();
        pendingRemovals.clear();

        tag.getAllKeys().forEach(key -> {
            try {
                long position = Long.parseLong(key);

                if (!tag.contains(key, Tag.TAG_COMPOUND)) {
                    QualityFood.LOG.warn("Skipping non-compound level data entry '{}'", key);
                    return;
                }

                Quality quality = Quality.CODEC.parse(NbtOps.INSTANCE, tag.getCompound(key)).resultOrPartial(QualityFood.LOG::error).orElse(Quality.NONE);

                if (!Quality.NONE.equals(quality)) {
                    qualities.put(position, quality);
                }
            } catch (NumberFormatException exception) {
                QualityFood.LOG.warn("Skipping malformed level data entry '{}'", key);
            }
        });
    }

    /** Passing {@code null} or {@link Quality#NONE} removes the entry */
    public static void set(final LevelAccessor level, final BlockPos position, final @Nullable Quality quality) {
        if (level instanceof ServerLevel serverLevel) {
            LevelData data = serverLevel.getData(AttachmentHandler.LEVEL_DATA);
            data.set(position, quality);
        }
    }

    /** @return The stored quality or a quality removed in the same game tick (since {@link de.cadentem.quality_food.mixin.LevelMixin} happens before the loot drops) if the flag is set to true */
    public static @NotNull Quality get(final LevelAccessor level, final BlockPos position, boolean queryLastRemoved) {
        Quality result = Quality.NONE;

        if (level instanceof ServerLevel serverLevel) {
            LevelData data = serverLevel.getData(AttachmentHandler.LEVEL_DATA);

            synchronized (data) {
                result = data.get(position);

                if (queryLastRemoved && Quality.NONE.equals(result)) {
                    // Only drops belonging to the removal of this block may consume the record
                    RemovedQuality removed = data.pendingRemovals.get(position.asLong());

                    if (removed != null && removed.gameTime() == serverLevel.getGameTime()
                            && data.pendingRemovals.remove(position.asLong(), removed)) {
                        result = removed.quality();
                    }
                }
            }
        }

        return result;
    }

    public static @NotNull Quality get(final LevelAccessor level, final BlockPos position) {
        return get(level, position, false);
    }

    private record RemovedQuality(Quality quality, long gameTime) { }
}
