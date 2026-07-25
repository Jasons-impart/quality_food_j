package de.cadentem.quality_food.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class EclipticSeasonsCompat {
    private static final String MOD_ID = "eclipticseasons";
    private static Method growChanceMethod;
    private static boolean lookupAttempted;

    private EclipticSeasonsCompat() {
    }

    public static float getGrowChance(final Level level, final BlockPos position, final BlockState state) {
        if (!ModList.get().isLoaded(MOD_ID)) {
            return 1.0F;
        }

        Method method = getGrowChanceMethod();
        if (method == null) {
            return 1.0F;
        }

        try {
            return (float) method.invoke(null, level, position, state);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            return 1.0F;
        }
    }

    private static Method getGrowChanceMethod() {
        if (lookupAttempted) {
            return growChanceMethod;
        }

        lookupAttempted = true;
        try {
            Class<?> detector = Class.forName("com.teamtea.eclipticseasons.common.item.GrowthDetectorItem");
            growChanceMethod = detector.getMethod("getGrowChance", Level.class, BlockPos.class, BlockState.class);
        } catch (ClassNotFoundException | NoSuchMethodException | LinkageError ignored) {
            growChanceMethod = null;
        }
        return growChanceMethod;
    }
}
