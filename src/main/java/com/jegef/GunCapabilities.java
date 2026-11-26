package com.jegef;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import ttv.migami.jeg.item.AnimatedBowItem;
import ttv.migami.jeg.item.GunItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.provider.ItemCapabilityProvider;

import java.util.Set;
import java.util.function.Function;

/**
 * Walks the item registry and hands every Just Enough Guns firearm an Epic Fight capability.
 *
 * <p>Called from a mixin at the head of {@code ItemCapabilityProvider.addDefaultItems()}, which
 * Epic Fight runs at the end of both its datapack reload and its client-side datapack sync. Doing
 * it there means the assignment survives a {@code /reload}, applies on both sides, and still loses
 * to an explicit datapack entry for the same item &mdash; {@code addDefaultItems} skips anything
 * already present, and a datapack weapon file is written before this point.</p>
 *
 * <p>Matching on the item class rather than on item ids means JEG add-ons that subclass
 * {@code GunItem} are covered without needing to be listed anywhere.</p>
 */
public final class GunCapabilities {
    private static int lastAssigned = -1;

    public static void applyAll() {
        if (!Config.assignGunCapabilities.get()) {
            return;
        }

        Set<String> pistols = Set.copyOf(Config.pistolItems.get());
        Set<String> excluded = Set.copyOf(Config.excludedItems.get());
        boolean bowsAsBows = Config.treatBowsAsBows.get();

        int assigned = 0;

        for (Item item : ForgeRegistries.ITEMS) {
            if (!(item instanceof GunItem)) {
                continue;
            }

            ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
            if (key == null || excluded.contains(key.toString())) {
                continue;
            }

            Function<Item, CapabilityItem.Builder> preset;

            if (bowsAsBows && item instanceof AnimatedBowItem) {
                preset = GunWeaponPresets.BOW_PRESET;
            } else if (pistols.contains(key.toString())) {
                preset = GunWeaponPresets.PISTOL_PRESET;
            } else {
                preset = GunWeaponPresets.GUN_PRESET;
            }

            try {
                ItemCapabilityProvider.put(item, preset.apply(item).build());
                assigned++;
            } catch (Exception e) {
                JegEpicFight.LOGGER.warn("Could not give {} an Epic Fight capability", key, e);
            }
        }

        // Only log when the count changes, so a reload loop does not spam the log.
        if (assigned != lastAssigned) {
            JegEpicFight.LOGGER.info("Gave {} Just Enough Guns item(s) an Epic Fight weapon capability", assigned);
            lastAssigned = assigned;
        }
    }

    private GunCapabilities() {
    }
}
