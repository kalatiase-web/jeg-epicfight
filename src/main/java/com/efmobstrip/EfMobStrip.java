package com.efmobstrip;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Removes Epic Fight's entity patches from vanilla mobs, so another mod -- Better Combat via
 * Better Mob Combat -- can animate them instead.
 *
 * <p>Epic Fight collects its mob patches into a map and fires
 * {@code EntityPatchRegistryEvent} over it before copying the result into its live registry, so a
 * listener running last can simply drop entries. A mob with no patch is left entirely alone by
 * Epic Fight: there is no class-based fallback that would catch it again (the only entry in Epic
 * Fight's by-class map is for arrows).</p>
 *
 * <p>No mixins, no code injected into either mod.</p>
 */
@Mod(EfMobStrip.MOD_ID)
public class EfMobStrip {
    public static final String MOD_ID = "efmobstrip";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EfMobStrip() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // LOWEST so this runs after Epic Fight and after any addon that adds its own patches.
        modBus.addListener(net.minecraftforge.eventbus.api.EventPriority.LOWEST, PatchStripper::onEntityPatchRegistry);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
