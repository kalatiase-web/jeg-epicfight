package com.bcplayeroff;

import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

/** Whether Epic Fight currently owns this player's animation. */
public final class EpicFightState {

    public static boolean shouldSuppress(Player player) {
        if (player == null) {
            return false;
        }

        if (!Config.onlyInEpicFightMode.get()) {
            return true;
        }

        try {
            PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
            return patch != null && patch.isEpicFightMode();
        } catch (Throwable t) {
            // If Epic Fight isn't there or changed shape, leave Better Combat alone rather than
            // breaking the player's animations entirely.
            return false;
        }
    }

    private EpicFightState() {
    }
}
