package de.cadentem.quality_food.util;

/** Shares a single quality roll between loot generation and the code that drops or collects that stack. */
public interface HarvestQualityRoll {
    boolean quality_food$startHarvestRoll();
}
