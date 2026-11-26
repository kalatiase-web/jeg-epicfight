package com.jegef;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.CapabilityItem.ZoomInType;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;

import java.util.function.Function;

/**
 * Epic Fight weapon-type presets for firearms.
 *
 * <p>Both presets are modelled on Epic Fight's own crossbow preset: the weapon is held across the
 * body in every living motion, and aim / shot / reload motions are mapped to the crossbow
 * animations. That is the closest fit in Epic Fight's stock animation set for something shouldered
 * and fired rather than drawn.</p>
 *
 * <p>Registering them through {@link WeaponCapabilityPresetRegistryEvent} also means a datapack can
 * use them by name, e.g. {@code data/jeg/capabilities/weapons/my_gun.json} containing
 * {@code { "type": "jegef:gun" }}.</p>
 */
public final class GunWeaponPresets {
    public static final ResourceLocation GUN = JegEpicFight.id("gun");
    public static final ResourceLocation GUN_PISTOL = JegEpicFight.id("gun_pistol");

    /** Two-handed firearms: rifles, shotguns, SMGs, launchers. */
    public static final Function<Item, CapabilityItem.Builder> GUN_PRESET = (item) ->
            RangedWeaponCapability.builder()
                    .zoomInType(ZoomInType.AIMING)
                    .addAnimationsModifier(LivingMotions.IDLE, Animations.BIPED_HOLD_CROSSBOW)
                    .addAnimationsModifier(LivingMotions.WALK, Animations.BIPED_HOLD_CROSSBOW)
                    .addAnimationsModifier(LivingMotions.RUN, Animations.BIPED_HOLD_CROSSBOW)
                    .addAnimationsModifier(LivingMotions.SNEAK, Animations.BIPED_HOLD_CROSSBOW)
                    .addAnimationsModifier(LivingMotions.KNEEL, Animations.BIPED_HOLD_CROSSBOW)
                    .addAnimationsModifier(LivingMotions.SWIM, Animations.BIPED_HOLD_CROSSBOW)
                    .addAnimationsModifier(LivingMotions.FLOAT, Animations.BIPED_HOLD_CROSSBOW)
                    .addAnimationsModifier(LivingMotions.FALL, Animations.BIPED_HOLD_CROSSBOW)
                    .addAnimationsModifier(LivingMotions.RELOAD, Animations.BIPED_CROSSBOW_RELOAD)
                    .addAnimationsModifier(LivingMotions.AIM, Animations.BIPED_CROSSBOW_AIM)
                    .addAnimationsModifier(LivingMotions.SHOT, Animations.BIPED_CROSSBOW_SHOT)
                    .category(WeaponCategories.CROSSBOW);

    /**
     * Sidearms. Same animation family, but the idle/walk poses are left as the normal biped ones so
     * a pistol does not get held like a rifle, and the weapon may sit in the offhand.
     */
    public static final Function<Item, CapabilityItem.Builder> PISTOL_PRESET = (item) ->
            RangedWeaponCapability.builder()
                    .zoomInType(ZoomInType.AIMING)
                    .addAnimationsModifier(LivingMotions.IDLE, Animations.BIPED_IDLE)
                    .addAnimationsModifier(LivingMotions.WALK, Animations.BIPED_WALK)
                    .addAnimationsModifier(LivingMotions.RELOAD, Animations.BIPED_CROSSBOW_RELOAD)
                    .addAnimationsModifier(LivingMotions.AIM, Animations.BIPED_CROSSBOW_AIM)
                    .addAnimationsModifier(LivingMotions.SHOT, Animations.BIPED_CROSSBOW_SHOT)
                    .category(WeaponCategories.CROSSBOW);

    /** JEG's bows are gun items in code, so they need Epic Fight's bow moveset explicitly. */
    public static final Function<Item, CapabilityItem.Builder> BOW_PRESET = (item) ->
            RangedWeaponCapability.builder()
                    .zoomInType(ZoomInType.USE_TICK)
                    .addAnimationsModifier(LivingMotions.IDLE, Animations.BIPED_IDLE)
                    .addAnimationsModifier(LivingMotions.WALK, Animations.BIPED_WALK)
                    .addAnimationsModifier(LivingMotions.AIM, Animations.BIPED_BOW_AIM)
                    .addAnimationsModifier(LivingMotions.SHOT, Animations.BIPED_BOW_SHOT)
                    .category(WeaponCategories.BOW);

    public static void onRegisterPresets(WeaponCapabilityPresetRegistryEvent event) {
        event.getTypeEntry().put(GUN, GUN_PRESET);
        event.getTypeEntry().put(GUN_PISTOL, PISTOL_PRESET);
        JegEpicFight.LOGGER.debug("Registered Epic Fight weapon presets {} and {}", GUN, GUN_PISTOL);
    }

    private GunWeaponPresets() {
    }
}
