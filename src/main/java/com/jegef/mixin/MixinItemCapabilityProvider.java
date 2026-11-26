package com.jegef.mixin;

import com.jegef.GunCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.provider.ItemCapabilityProvider;

/**
 * Epic Fight calls {@code addDefaultItems()} as the last step of both its datapack reload and its
 * client-side datapack sync, so it is the one place that is guaranteed to run on both sides every
 * time capabilities are rebuilt. Injecting at the head lets gun capabilities be registered before
 * Epic Fight's class-based fallback would otherwise hand every firearm the bare-fist capability.
 */
@Mixin(ItemCapabilityProvider.class)
public class MixinItemCapabilityProvider {

    @Inject(method = "addDefaultItems", at = @At("HEAD"), remap = false)
    private static void jegef$assignGunCapabilities(CallbackInfo ci) {
        GunCapabilities.applyAll();
    }
}
