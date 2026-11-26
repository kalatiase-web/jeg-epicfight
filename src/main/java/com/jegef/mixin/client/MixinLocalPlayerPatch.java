package com.jegef.mixin.client;

import com.jegef.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ttv.migami.jeg.item.GunItem;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

/**
 * The fix that actually lets guns be fired.
 *
 * <p>In Epic Fight mode, {@code canPlayAttackAnimation()} decides whether the attack button belongs
 * to Epic Fight or to the game underneath. It does not look at what is being held, so a left click
 * with a rifle plays a melee swing and the gun never fires. Returning false while a JEG gun is in
 * the main hand gives the click back to JEG.</p>
 *
 * <p>Epic Fight reads the same method when deciding whether to draw its own crosshair and whether
 * to take over the first-person arms, so this also restores JEG's dynamic crosshair and its
 * first-person gun model as a side effect.</p>
 */
@Mixin(LocalPlayerPatch.class)
public class MixinLocalPlayerPatch {

    @Inject(method = "canPlayAttackAnimation", at = @At("HEAD"), cancellable = true, remap = false)
    private void jegef$releaseAttackKeyForGuns(CallbackInfoReturnable<Boolean> cir) {
        if (!Config.freeLeftClickWithGuns.get()) {
            return;
        }

        LocalPlayer player = ((LocalPlayerPatch) (Object) this).getOriginal();
        if (player == null) {
            return;
        }

        ItemStack held = player.getMainHandItem();
        if (held.getItem() instanceof GunItem) {
            cir.setReturnValue(false);
        }
    }
}
