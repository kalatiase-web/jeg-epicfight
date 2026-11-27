package com.bcplayeroff;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Config {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue suppressPose;
    public static final ForgeConfigSpec.BooleanValue suppressAttackAnimation;
    public static final ForgeConfigSpec.BooleanValue onlyInEpicFightMode;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("player");

        suppressPose = b
                .comment("Stop Better Combat applying its weapon stance to you.",
                        "This is the one that fixes snapping back into a Better Combat pose",
                        "the moment an Epic Fight attack animation finishes.")
                .define("suppressPose", true);

        suppressAttackAnimation = b
                .comment("Stop Better Combat playing its swing animation on you.",
                        "Epic Fight already wins this one while it has control, but turning it",
                        "off avoids two animation systems starting at the same moment.")
                .define("suppressAttackAnimation", true);

        onlyInEpicFightMode = b
                .comment("Only suppress while you are actually in Epic Fight mode.",
                        "Leave true and toggling to vanilla mode gives you Better Combat back.",
                        "Set false to keep Better Combat off you at all times.")
                .define("onlyInEpicFightMode", true);

        b.pop();
        SPEC = b.build();
    }

    private Config() {
    }
}
