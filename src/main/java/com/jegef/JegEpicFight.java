package com.jegef;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Compatibility bridge between Just Enough Guns and Epic Fight on Forge 1.20.1.
 *
 * <p>Three things happen here:</p>
 * <ol>
 *   <li>Two Epic Fight weapon-type presets ({@code jegef:gun}, {@code jegef:gun_pistol}) are
 *       registered, so JEG firearms are understood by Epic Fight as ranged weapons rather than
 *       falling back to the bare-fist capability.</li>
 *   <li>Every {@code GunItem} in the item registry is assigned one of those presets, which also
 *       covers JEG add-on mods such as Mo' Than Enough Guns.</li>
 *   <li>Left click is handed back to JEG while a gun is held, so firing works in Epic Fight mode,
 *       and JEG's fire/reload events drive Epic Fight's shot and reload animations.</li>
 * </ol>
 */
@Mod(JegEpicFight.MOD_ID)
public class JegEpicFight {
    public static final String MOD_ID = "jegef";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JegEpicFight() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(GunWeaponPresets::onRegisterPresets);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        MinecraftForge.EVENT_BUS.register(GunEventBridge.class);
        MinecraftForge.EVENT_BUS.register(MobArmingGuard.class);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
