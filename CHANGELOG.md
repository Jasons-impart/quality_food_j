# Changelog

## 2.3.6-cdpr.1

CDPR-maintained replacement build based on upstream Quality Food 2.3.6 for Minecraft 1.21.1 and NeoForge.

### Added

- Quality propagation for FTB Ultimine right-click harvesting.
- Fruits Delight support for fruit leaves, single and double fruit bushes, quality-bearing saplings and generated trees, L2Harvester, and falling durians.
- Create mechanical harvester quality propagation.
- Ratatouille oven, squeeze basin, and thresher output propagation.
- Refurbished Furniture frying pan and stove output propagation, including safe output-capacity checks.
- Ecliptic Seasons harvest chance adjustment with effective base-position handling for multi-block crops.
- A `quality_food:quality_crops` tag that imports the CDPR `createdelightcore:quality_crops` crop set.
- Automated GitHub and CurseForge release workflows for CurseForge project `1623542`.

### Changed

- Crafting is strict: if any quality-applicable ingredient has no quality, the result does not retain or roll quality from the remaining ingredients.
- Drop contexts now preserve and restore nested harvest operations.
- Crop checks account for maturity, multi-block base positions, effective farmland, and My Nether's Delight `lit` crops.
- Sophisticated Core compacting compatibility supports both the CDPR 1.4.36 signature and newer 1.4.54 releases.
- Fork metadata identifies this build as `2.3.6-cdpr.1`, maintained by Cadentem and JSI Team.

### Fixed

- Prevented `SpecialContainer` output checks from indexing beyond a container's available slots.
- Fixed Ratatouille squeeze basin injection compatibility with version 1.4.0.
- Preserved seasonal harvest calculations for automated harvesters and FTB Ultimine.
- Stored falling durian quality per entity so unrelated block removals cannot overwrite it while the durian is falling.
- Removed the stale `quality_food.refmap.json` declaration, which referenced a file not packaged by the upstream build.

### Compatibility

- This jar keeps the `quality_food` mod id and is a drop-in replacement for upstream Quality Food. Do not install both jars at the same time.
- Optional integrations are gated and do not require every supported mod to be installed.
