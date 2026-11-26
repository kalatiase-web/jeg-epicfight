package com.jegef.mixin;

import com.jegef.Config;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ttv.migami.jeg.item.GunItem;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

/**
 * Keeps Epic Fight from giving a melee moveset to a mob that is holding a gun.
 *
 * <p>{@code getHoldingItemWeaponMotionBuilder} decides which combat behaviours a mob gets, and its
 * fallback is: main hand not empty, therefore one-handed melee tools. Epic Fight's lookup table has
 * no entry for the crossbow category either, so a gun lands on that fallback regardless. The mob
 * then carries both Epic Fight's {@code AnimatedAttackGoal} and {@code TargetChasingGoal} and JEG's
 * {@code GunAttackGoal}: it charges into melee range to swing a rifle while simultaneously trying
 * to hold position and shoot, and plays melee swing animations as it fires.</p>
 *
 * <p>Returning null is Epic Fight's own signal for "no combat behaviour" &mdash; every call site
 * checks for it &mdash; so the mob keeps its gun AI and nothing else. The holding pose is
 * unaffected, as that comes from the item capability rather than from here.</p>
 */
@Mixin(HumanoidMobPatch.class)
public class MixinHumanoidMobPatch {

    @Inject(method = "getHoldingItemWeaponMotionBuilder", at = @At("HEAD"), cancellable = true, remap = false)
    private void jegef$noMeleeBehaviourForGunners(CallbackInfoReturnable<CombatBehaviors.Builder<HumanoidMobPatch<?>>> cir) {
        if (!Config.suppressMobMeleeWithGuns.get()) {
            return;
        }

        PathfinderMob mob = ((HumanoidMobPatch<?>) (Object) this).getOriginal();

        if (mob != null && mob.getMainHandItem().getItem() instanceof GunItem) {
            cir.setReturnValue(null);
        }
    }
}
