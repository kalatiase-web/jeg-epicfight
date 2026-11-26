package com.efmobstrip;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PatchStripper {

    /**
     * Never stripped, whatever the config says.
     *
     * <p>The player's patch is Epic Fight itself -- removing it would turn the mod off for you.
     * The projectile patches are not animations; Epic Fight uses them for its own hit handling,
     * and dropping them breaks ranged combat rather than changing how anything looks.</p>
     */
    private static final Set<String> NEVER_STRIP = Set.of(
            "minecraft:player",
            "minecraft:arrow",
            "minecraft:spectral_arrow",
            "minecraft:wither_skull",
            "minecraft:dragon_fireball",
            "minecraft:trident"
    );

    public static void onEntityPatchRegistry(EntityPatchRegistryEvent event) {
        Set<String> kept = Set.copyOf(Config.keptMobs.get());
        Set<String> extra = Set.copyOf(Config.extraStrippedMobs.get());
        boolean stripVanilla = Config.stripAllVanillaMobs.get();

        List<String> removed = new ArrayList<>();

        event.getTypeEntry().keySet().removeIf(type -> {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);

            if (id == null) {
                return false;
            }

            String name = id.toString();

            if (NEVER_STRIP.contains(name) || kept.contains(name)) {
                return false;
            }

            // Leave anything that isn't a living mob alone -- projectiles and the like.
            if (!isLiving(type)) {
                return false;
            }

            boolean strip = extra.contains(name)
                    || (stripVanilla && "minecraft".equals(id.getNamespace()));

            if (strip) {
                removed.add(name);
            }

            return strip;
        });

        if (removed.isEmpty()) {
            EfMobStrip.LOGGER.info("Left every Epic Fight mob patch in place");
        } else {
            removed.sort(String::compareTo);
            EfMobStrip.LOGGER.info("Removed the Epic Fight patch from {} mob(s): {}",
                    removed.size(), String.join(", ", removed));
        }
    }

    /**
     * True for entity types that have a moveset worth stripping.
     *
     * <p>Projectiles sit in {@link MobCategory#MISC}, so the category is a good first filter --
     * but so do golems and the player, which is why those are named explicitly.</p>
     */
    private static boolean isLiving(EntityType<?> type) {
        return type.getCategory() != MobCategory.MISC
                || type == EntityType.IRON_GOLEM
                || type == EntityType.SNOW_GOLEM;
    }

    private PatchStripper() {
    }
}
