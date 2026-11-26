package com.jegef;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ttv.migami.jeg.item.GunItem;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

/**
 * Stops Just Enough Guns from arming mobs that are already carrying a weapon.
 *
 * <p>JEG tags eligible mobs {@code MobGunner} when they spawn, then swaps a gun into their main hand
 * on their first couple of ticks, replacing whatever was there. A skeleton's bow, a zombie's sword,
 * a piglin's crossbow &mdash; all overwritten, which also throws away the Epic Fight moveset that
 * came with the weapon.</p>
 *
 * <p>This listener runs at {@link EventPriority#HIGHEST}, so it sees the mob on the same tick but
 * before JEG does, and drops the tag when the mob is already armed. Equipment is populated during
 * spawn finalization, so by the first tick there is something real to inspect &mdash; which is why
 * this hooks the tick rather than the spawn event.</p>
 */
public final class MobArmingGuard {

    /** Marker so a mob is only inspected once, even if the tick event fires repeatedly. */
    private static final String CHECKED_TAG = "jegef_arming_checked";
    private static final String JEG_GUNNER_TAG = "MobGunner";

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMobTick(LivingEvent.LivingTickEvent event) {
        if (!Config.skipAlreadyArmedMobs.get()) {
            return;
        }

        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide() || !(entity instanceof PathfinderMob mob)) {
            return;
        }

        // JEG only arms during the first ticks of a mob's life; past that there is nothing to stop.
        if (mob.tickCount >= 2) {
            return;
        }

        if (!mob.getTags().contains(JEG_GUNNER_TAG) || mob.getTags().contains(CHECKED_TAG)) {
            return;
        }

        mob.addTag(CHECKED_TAG);

        if (!isAlreadyArmed(mob)) {
            return;
        }

        mob.removeTag(JEG_GUNNER_TAG);

        JegEpicFight.LOGGER.debug("Left {} with its own weapon instead of handing it a gun",
                mob.getType());
    }

    /**
     * True when this mob is carrying something that already counts as a weapon.
     *
     * <p>Epic Fight's own capability is the primary test, since "already has a weapon" really means
     * "already has an Epic Fight moveset we would be destroying". Anything Epic Fight files under a
     * real weapon category counts; bare fists and non-weapons do not. Vanilla item classes are
     * checked as a fallback for the case where capabilities have not been built yet.</p>
     */
    private static boolean isAlreadyArmed(PathfinderMob mob) {
        if (isWeapon(mob.getMainHandItem())) {
            return true;
        }

        return Config.countOffhandAsArmed.get() && isWeapon(mob.getOffhandItem());
    }

    private static boolean isWeapon(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        // A gun means JEG already got here, or the mob was built as a gunner. Not our business.
        if (stack.getItem() instanceof GunItem) {
            return false;
        }

        CapabilityItem cap = EpicFightCapabilities.getItemStackCapability(stack);

        if (cap != null && !cap.isEmpty()) {
            WeaponCategory category = cap.getWeaponCategory();

            if (category != WeaponCategories.FIST && category != WeaponCategories.NOT_WEAPON) {
                return true;
            }
        }

        return stack.getItem() instanceof TieredItem
                || stack.getItem() instanceof ProjectileWeaponItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof ShieldItem;
    }

    private MobArmingGuard() {
    }
}
