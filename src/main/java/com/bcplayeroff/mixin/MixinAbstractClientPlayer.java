package com.bcplayeroff.mixin;

import com.bcplayeroff.Config;
import com.bcplayeroff.EpicFightState;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Better Combat adds its player animation handling to {@code AbstractClientPlayer} by mixin:
 * four pose layers plus {@code updateAnimationsOnTick()} and {@code playAttackAnimation(...)}.
 * This cancels those two methods so Epic Fight keeps the player.
 *
 * <p>Priority 1500 matters. Mixins apply lowest-priority-first, and Better Combat's is at the
 * default 1000 — running after it is what makes these methods exist to inject into.</p>
 *
 * <p>{@code require = 0} on both injections is deliberate: if a future Better Combat renames or
 * drops a method, this mod quietly does nothing instead of crashing the game on launch. The
 * symptom of that would be the old snap-back returning, not a broken instance.</p>
 */
@Mixin(value = AbstractClientPlayer.class, priority = 1500)
public class MixinAbstractClientPlayer {

    @Inject(method = "updateAnimationsOnTick", at = @At("HEAD"), cancellable = true,
            remap = false, require = 0)
    private void bcplayeroff$dropWeaponStance(CallbackInfo ci) {
        if (!Config.suppressPose.get()) {
            return;
        }
        if (EpicFightState.shouldSuppress((AbstractClientPlayer) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "playAttackAnimation", at = @At("HEAD"), cancellable = true,
            remap = false, require = 0)
    private void bcplayeroff$dropAttackAnimation(CallbackInfo ci) {
        if (!Config.suppressAttackAnimation.get()) {
            return;
        }
        if (EpicFightState.shouldSuppress((AbstractClientPlayer) (Object) this)) {
            ci.cancel();
        }
    }
}
