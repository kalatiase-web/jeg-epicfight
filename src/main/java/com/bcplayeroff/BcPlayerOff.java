package com.bcplayeroff;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * Stops Better Combat driving the player's pose, so Epic Fight keeps the player to itself.
 *
 * <p>Better Combat holds four pose layers on the client player -- main/off hand body and item
 * poses -- and re-applies them every tick from {@code PlayerEntityMixin.post_Tick}, which calls
 * {@code updateAnimationsOnTick()}. Epic Fight's 20.14.6 change gave its <em>action</em>
 * animations priority over Player Animator, which is why an attack plays correctly, but a weapon
 * stance is a pose rather than an action, so nothing stops Better Combat reasserting it the moment
 * the attack ends. That is the snap-back.</p>
 *
 * <p>This cancels the two entry points on the client player -- the per-tick pose update and the
 * attack animation -- while the player is in Epic Fight mode. Mobs are untouched, so Better Mob
 * Combat keeps working; it is Better Combat's mob path, not this one.</p>
 */
@Mod(BcPlayerOff.MOD_ID)
public class BcPlayerOff {
    public static final String MOD_ID = "bcplayeroff";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BcPlayerOff() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }
}
