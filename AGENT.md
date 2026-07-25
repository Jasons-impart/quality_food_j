# Quality Food — CDPR Fork

This repository is the CDPR-maintained fork of Quality Food for Minecraft 1.21.1 and NeoForge.

## Identity and compatibility

- Keep the mod id and data namespace as `quality_food`.
- This jar is a drop-in replacement for upstream Quality Food, not an addon; it must not be installed alongside the original jar.
- Preserve existing worlds, item Data Components, quality type registry entries, configs, and datapack identifiers whenever possible.
- Keep upstream attribution and the MIT license intact.
- Versions use the upstream base plus a CDPR suffix, for example `2.3.6-cdpr.1`.

## Repository boundaries

This fork owns Quality Food behavior:

- harvest and crop-quality calculation;
- crafting, cooking, compacting, and machine-output propagation;
- optional integrations required by the active CDPR pack;
- fixes to Quality Food internals needed for those behaviors.

Create Delight Core owns CDC gameplay and economy features. Do not move the quality absorber, Lightman's Currency exchange, CDC blocks/items, or unrelated pack migration work into this repository.

The CDC migration ledger lives in `/home/halo/Desktop/gitRepo/JSI/Create-Delight-Core/docs/status.md`. Do not create a second migration ledger here.

## Optional-mod compatibility

- A missing optional mod must never prevent Quality Food from loading.
- Put conditional mixins in a package named after the target mod id so `ApplyMixinPlugin` can gate them.
- Prefer string mixin targets when a compile dependency is unnecessary.
- Verify target signatures against the exact CDPR jar or public source before writing an injection.
- Prefer Maven or CurseMaven dependencies; do not add local jar or `flatDir` dependencies.
- Use public source or sources jars first, then `javap`, and decompile only the required target classes if needed.

Active compatibility references are in:

`/home/halo/Desktop/myPrism/PrismLauncher-Linux-Qt6-Portable-11.0.2/instances/CDPR/minecraft/mods`

## Quality propagation rules

- Preserve nested `DropData` contexts with `push`/`pop`; do not overwrite an outer player or automation context.
- Keep player, level, position, crop state, block quality, and effective farmland available when probability calculation needs them.
- Multi-block crops must resolve their effective base position before farmland or season calculations.
- CDPR crafting is strict: if any quality-applicable ingredient has no quality, the result must not retain or roll quality from the other ingredients.
- Do not reintroduce obsolete CDC mixins when upstream 2.3.6 already implements the behavior correctly.

## Verification

Before handoff, run:

```bash
./gradlew compileJava processResources jar --no-configuration-cache
git diff --check
```

For mixin changes, also deploy the jar to the CDPR instance and inspect `logs/latest.log` and `logs/debug.log` for `InvalidInjection`, `MixinApplyError`, and Quality Food fatal entries.

## Releases

- Releases are built from branch `1.21.1`.
- Tags use `v<mod_version>`, for example `v2.3.6-cdpr.1`.
- `.github/workflows/release.yml` publishes GitHub Releases and CurseForge project `1623542`.
- The GitHub repository must provide the `CURSEFORGE_TOKEN` secret.
- Do not tag, publish, or push unless explicitly requested.
