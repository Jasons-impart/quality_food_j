package de.cadentem.quality_food.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Bridge to Create Delight Core's quality harvest automation. CDC keeps its own
 * harvest context; this class calls into it reflectively so the fork compiles
 * against upstream Quality Food only.
 */
public final class HarvestAutomationCompat {
    private static final String CDC_MOD_ID = "createdelightcore";
    private static final String CONTEXT_CLASS = "io.github.jasonsimpart.content.quality.harvest.QualityHarvestAutomationContext";
    private static volatile boolean lookupAttempted;
    private static volatile Method getSettingsMethod;
    private static volatile Method getGrowChanceMethod;
    private static volatile Method extraCropMethod;
    private static volatile Method maxQualityMethod;
    private static volatile Method multiplierMethod;

    private HarvestAutomationCompat() {
    }

    /**
     * Returns the active automation settings when a contraption harvest context is present
     * and the crop is relevant. Calling this may consume life matter on first activation.
     */
    public static @Nullable Settings getSettings(final BlockState state) {
        if (!isLoaded()) {
            return null;
        }
        try {
            Object settings = getSettingsMethod().invoke(null, state);
            if (settings == null) {
                return null;
            }
            int maxQuality = (int) maxQualityMethod.invoke(settings);
            float multiplier = (float) multiplierMethod.invoke(settings);
            return new Settings(maxQuality, multiplier);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            return null;
        }
    }

    /**
     * Season-based grow chance factor for automated harvests (no player bonus).
     */
    public static float getGrowChance(final Level level, final BlockPos position, final BlockState state, final int sourceRank) {
        if (!isLoaded()) {
            return 1.0F;
        }
        try {
            return (float) getGrowChanceMethod().invoke(null, level, position, state, sourceRank);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            return 1.0F;
        }
    }

    private static boolean isLoaded() {
        return ModList.get() != null && ModList.get().isLoaded(CDC_MOD_ID);
    }

    private static Method getSettingsMethod() throws ReflectiveOperationException {
        if (getSettingsMethod == null) {
            if (lookupAttempted) {
                throw new NoSuchMethodException(CONTEXT_CLASS);
            }
            lookupAttempted = true;
            synchronized (HarvestAutomationCompat.class) {
                if (getSettingsMethod == null) {
                    Class<?> context = Class.forName(CONTEXT_CLASS);
                    Method settings = context.getMethod("getAutomationSettings", BlockState.class);
                    Method growChance = context.getMethod("getAutomatedGrowChance", Level.class, BlockPos.class, BlockState.class, int.class);
                    Method extraCrop = context.getMethod("isExtraCrop", BlockState.class);
                    Class<?> settingsClass = Class.forName(CONTEXT_CLASS + "$Settings");
                    // Publish all dependent fields before the getSettingsMethod sentinel.
                    getGrowChanceMethod = growChance;
                    extraCropMethod = extraCrop;
                    maxQualityMethod = settingsClass.getMethod("maxQuality");
                    multiplierMethod = settingsClass.getMethod("multiplier");
                    getSettingsMethod = settings;
                }
            }
        }
        return getSettingsMethod;
    }

    private static Method getGrowChanceMethod() throws ReflectiveOperationException {
        getSettingsMethod();
        return getGrowChanceMethod;
    }

    /** Additional crop blocks CDC considers harvestable (createdelightcore:quality_crops tag). */
    public static boolean isExtraCrop(final BlockState state) {
        if (!isLoaded()) {
            return false;
        }
        try {
            getSettingsMethod();
            return (boolean) extraCropMethod.invoke(null, state);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            return false;
        }
    }

    public record Settings(int maxQuality, float multiplier) {
    }
}
