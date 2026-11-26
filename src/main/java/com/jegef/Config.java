package com.jegef;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class Config {
    public static final ForgeConfigSpec SPEC;

    /** Master switch for assigning Epic Fight capabilities to JEG guns. */
    public static final ForgeConfigSpec.BooleanValue assignGunCapabilities;
    /** Hand left click back to JEG while a gun is held, so the gun fires instead of swinging. */
    public static final ForgeConfigSpec.BooleanValue freeLeftClickWithGuns;
    /** Play Epic Fight's shot animation when a gun is fired. */
    public static final ForgeConfigSpec.BooleanValue playShotAnimation;
    /** Play Epic Fight's reload animation when a gun is reloaded. */
    public static final ForgeConfigSpec.BooleanValue playReloadAnimation;
    /** Give JEG bows Epic Fight's bow moveset instead of the gun moveset. */
    public static final ForgeConfigSpec.BooleanValue treatBowsAsBows;
    /** Item ids that should use the lighter one-handed gun preset. */
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> pistolItems;
    /** Item ids that should be left entirely alone. */
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> excludedItems;
    /** Don't let JEG hand a gun to a mob that already carries a weapon. */
    public static final ForgeConfigSpec.BooleanValue skipAlreadyArmedMobs;
    /** Count a weapon in the offhand as "already armed". */
    public static final ForgeConfigSpec.BooleanValue countOffhandAsArmed;
    /** Stop Epic Fight giving melee combat behaviour to mobs holding a gun. */
    public static final ForgeConfigSpec.BooleanValue suppressMobMeleeWithGuns;

    private static final List<String> DEFAULT_PISTOLS = List.of(
            "jeg:revolver",
            "jeg:semi_auto_pistol",
            "jeg:combat_pistol",
            "jeg:flare_gun",
            "jeg:finger_gun"
    );

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("capabilities");
        assignGunCapabilities = b
                .comment("Assign Epic Fight weapon capabilities to Just Enough Guns firearms.",
                        "Turn this off and guns fall back to Epic Fight's bare-fist handling.")
                .define("assignGunCapabilities", true);
        treatBowsAsBows = b
                .comment("JEG's bows extend its gun item, so they are guns as far as code is concerned.",
                        "With this on they get Epic Fight's bow aim/shot moveset instead of the gun one.")
                .define("treatBowsAsBows", true);
        pistolItems = b
                .comment("Item ids that should use the lighter one-handed gun preset.")
                .defineList("pistolItems", DEFAULT_PISTOLS, o -> o instanceof String);
        excludedItems = b
                .comment("Item ids this mod should not touch at all.")
                .defineList("excludedItems", List.of(), o -> o instanceof String);
        b.pop();

        b.push("mobs");
        skipAlreadyArmedMobs = b
                .comment("Just Enough Guns tags eligible mobs on spawn and then swaps a gun into",
                        "their main hand, replacing whatever they were already holding -- and the",
                        "Epic Fight moveset that came with it. With this on, mobs that already",
                        "carry a weapon keep it and are never turned into gunners.")
                .define("skipAlreadyArmedMobs", true);
        countOffhandAsArmed = b
                .comment("Also treat a weapon or shield in the offhand as already armed.")
                .define("countOffhandAsArmed", false);
        suppressMobMeleeWithGuns = b
                .comment("Epic Fight falls back to a one-handed melee moveset for any non-empty",
                        "main hand, so gun-carrying mobs end up with both Epic Fight's melee chase",
                        "goals and JEG's shooting goal at once: they run in swinging a rifle while",
                        "trying to shoot. With this on, mobs holding a gun get no Epic Fight combat",
                        "behaviour and JEG's gun AI runs alone. Their holding pose is unaffected.")
                .define("suppressMobMeleeWithGuns", true);
        b.pop();

        b.push("controls");
        freeLeftClickWithGuns = b
                .comment("While a JEG gun is held, stop Epic Fight from claiming the attack button",
                        "so that left click fires the gun. Without this, guns cannot be shot in",
                        "Epic Fight mode.")
                .define("freeLeftClickWithGuns", true);
        b.pop();

        b.push("animations");
        playShotAnimation = b
                .comment("Play Epic Fight's shot animation when a JEG gun fires.")
                .define("playShotAnimation", true);
        playReloadAnimation = b
                .comment("Play Epic Fight's reload animation when a JEG gun is reloaded.")
                .define("playReloadAnimation", true);
        b.pop();

        SPEC = b.build();
    }

    private Config() {
    }
}
