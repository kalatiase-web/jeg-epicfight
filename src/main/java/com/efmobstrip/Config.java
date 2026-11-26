package com.efmobstrip;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class Config {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue stripAllVanillaMobs;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> keptMobs;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> extraStrippedMobs;

    /**
     * Bosses keep their Epic Fight movesets. Warden and Elder Guardian are listed even though Epic
     * Fight does not currently patch them, so that they stay protected if a future version does.
     */
    private static final List<String> DEFAULT_KEPT = List.of(
            "minecraft:ender_dragon",
            "minecraft:wither",
            "minecraft:warden",
            "minecraft:elder_guardian"
    );

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.comment("Which mobs lose their Epic Fight patch.",
                  "A mob with no patch uses its normal animations and AI, which is what lets",
                  "Better Combat (via Better Mob Combat) take over.").push("stripping");

        stripAllVanillaMobs = b
                .comment("Strip every vanilla (minecraft:) mob Epic Fight patches, except the ones",
                        "listed in keptMobs. Modded mobs are never touched by this option.")
                .define("stripAllVanillaMobs", true);

        keptMobs = b
                .comment("Mobs that keep their Epic Fight moveset no matter what.",
                        "Raid bosses are worth considering here too:",
                        "  minecraft:ravager, minecraft:evoker, minecraft:piglin_brute")
                .defineList("keptMobs", DEFAULT_KEPT, o -> o instanceof String);

        extraStrippedMobs = b
                .comment("Additional entity ids to strip, for modded mobs.",
                        "Example: [\"somemod:bandit\", \"othermod:knight\"]")
                .defineList("extraStrippedMobs", List.of(), o -> o instanceof String);

        b.pop();
        SPEC = b.build();
    }

    private Config() {
    }
}
