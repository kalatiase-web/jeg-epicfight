package com.jegef;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ttv.migami.jeg.event.GunFireEvent;
import ttv.migami.jeg.event.GunReloadEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * Turns Just Enough Guns' own fire and reload events into Epic Fight animations.
 *
 * <p>Both events are posted server-side by JEG, so playing the animation synchronized from here
 * gets it to the firing player and to everyone tracking them &mdash; which is what makes other
 * players' guns look like they are being fired rather than swung.</p>
 */
public final class GunEventBridge {

    @SubscribeEvent
    public static void onGunFired(GunFireEvent.Post event) {
        if (!Config.playShotAnimation.get() || event.isClient()) {
            return;
        }
        ServerPlayerPatch patch = patchOf(event.getEntity());
        if (patch != null) {
            patch.playAnimationSynchronized(Animations.BIPED_CROSSBOW_SHOT, 0.0F);
        }
    }

    @SubscribeEvent
    public static void onGunReloaded(GunReloadEvent.Post event) {
        if (!Config.playReloadAnimation.get() || event.isClient()) {
            return;
        }
        ServerPlayerPatch patch = patchOf(event.getEntity());
        if (patch != null) {
            patch.playAnimationSynchronized(Animations.BIPED_CROSSBOW_RELOAD, 0.0F);
        }
    }

    /** Returns the player's Epic Fight patch, or null when they are in vanilla mode. */
    private static ServerPlayerPatch patchOf(Player player) {
        ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
        return (patch != null && patch.isEpicFightMode()) ? patch : null;
    }

    private GunEventBridge() {
    }
}
