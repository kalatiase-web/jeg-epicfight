# JEG x Epic Fight

A Forge 1.20.1 compatibility mod that makes [Just Enough Guns](https://www.curseforge.com/minecraft/mc-mods/just-enough-guns)
firearms work with [Epic Fight](https://www.curseforge.com/minecraft/mc-mods/epic-fight-mod),
in the spirit of what Tijōn's Epic Arsenal does for TaCZ.

## What it does

| Problem | Fix |
| --- | --- |
| Guns can't be fired in Epic Fight mode — left click plays a melee swing | A mixin on `LocalPlayerPatch.canPlayAttackAnimation` returns `false` while a JEG gun is held, handing the attack button back to JEG. This also restores JEG's dynamic crosshair and first-person gun rendering, since Epic Fight reads the same method for both. |
| Epic Fight treats guns as bare fists, so they get a punch moveset and no holding pose | Two weapon presets (`jegef:gun`, `jegef:gun_pistol`) built on `RangedWeaponCapability`, assigned to every `GunItem` in the registry. |
| No shoot/reload animation for other players | JEG's server-side `GunFireEvent.Post` / `GunReloadEvent.Post` are bridged to Epic Fight's synchronized shot and reload animations. |
| JEG's bows are gun items in code | Detected separately and given Epic Fight's bow moveset. |
| JEG overwrites a mob's existing weapon with a gun, throwing away its Epic Fight moveset | A `LivingTickEvent` listener at `HIGHEST` priority drops JEG's `MobGunner` tag before JEG acts on it, when the mob is already holding something Epic Fight files as a real weapon. |
| Gun-carrying mobs get Epic Fight melee goals *and* JEG's shooting goal, so they charge in swinging a rifle while trying to shoot | A mixin on `HumanoidMobPatch.getHoldingItemWeaponMotionBuilder` returns `null` for gun holders — Epic Fight's own "no combat behaviour" signal — leaving JEG's gun AI in sole charge. Holding pose is unaffected. |

Because capabilities are matched on item **class**, JEG add-ons that subclass `GunItem`
(Mo' Than Enough Guns and similar) are covered automatically.

## Requirements

- Minecraft 1.20.1, Forge 47.3.0+
- Epic Fight 20.9 or newer (built against 20.15.2)
- Just Enough Guns 0.13 or newer (built against 0.14.5)

## Configuration

`config/jegef-common.toml`:

- `assignGunCapabilities` — master switch for the capability assignment
- `treatBowsAsBows` — give JEG bows the bow moveset instead of the gun one
- `pistolItems` / `excludedItems` — per-item overrides
- `skipAlreadyArmedMobs` — mobs that already carry a weapon are never turned into gunners
- `countOffhandAsArmed` — also count an offhand weapon or shield (off by default)
- `suppressMobMeleeWithGuns` — strip Epic Fight's melee behaviour from gun-carrying mobs
- `freeLeftClickWithGuns` — turn off if another mod already frees the attack button
- `playShotAnimation` / `playReloadAnimation`

## Building

Pushing to `main` builds the jar on GitHub Actions; download it from the run's
**jeg-epicfight-jar** artifact. Locally: `gradle build` with JDK 17.

Dependency versions live in `gradle.properties`. `jeg_curse_file` is a CurseForge **file id** —
to target a different JEG release, take the id from its download page URL.

## Caveats

- Aim pose uses Epic Fight's `ZoomInType.AIMING`, which keys off vanilla item use. JEG aims with its
  own key, so the aim animation may not follow JEG's ADS state exactly.
- Epic Fight has no stock firearm animations, so the crossbow set is reused. Replacing these with
  purpose-made animations is the obvious next step.
