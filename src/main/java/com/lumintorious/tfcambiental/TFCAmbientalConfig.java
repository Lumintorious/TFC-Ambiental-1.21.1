package com.lumintorious.tfcambiental;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TFCAmbientalConfig
{

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue averageTemperature = BUILDER
        .comment("The average point for temperature, the not too warm and not too cool point")
        .defineInRange("averageTemperature", 15, 0, 30);

    public static final ModConfigSpec.DoubleValue hotThreshold = BUILDER
        .comment("The point where warmth starts to affect the screen, but only mildly")
        .defineInRange("hotThreshold", 20F, 5F, 35F);

    public static final ModConfigSpec.DoubleValue coolThreshold = BUILDER
        .comment("The point where cold starts to affect the screen, but only mildly")
        .defineInRange("coolThreshold", 10F, -15F, 25F);

    public static final ModConfigSpec.DoubleValue burnThreshold = BUILDER
        .comment("The point where warmth starts to hurt the player.json")
        .defineInRange("burnThreshold", 25F, 15F, 45F);

    public static final ModConfigSpec.DoubleValue freezeThreshold = BUILDER
        .comment("The point where cold starts to hurt the player.json")
        .defineInRange("freezeThreshold", 5F, -15F, 15F);

    public static final ModConfigSpec.DoubleValue wetnessChangeSpeed = BUILDER
        .comment("How quickly player.json wetness changes towards the target environment wetness")
        .defineInRange("wetnessChangeSpeed", 1F, 0F, 50F);

    public static final ModConfigSpec.DoubleValue temperatureChangeSpeed = BUILDER
        .comment("How quickly player.json temperature changes towards the target environment temperature")
        .defineInRange("temperatureChangeSpeed", 1F, 0F, 50F);

    public static final ModConfigSpec.DoubleValue goodTemperatureChangeSpeed = BUILDER
        .comment("How quickly player.json temperature changes towards the target environment temperature when it's beneficial to do so")
        .defineInRange("goodTemperatureChangeSpeed", 4F, 0F, 50F);

    public static final ModConfigSpec.DoubleValue badTemperatureChangeSpeed = BUILDER
        .comment("How quickly player.json temperature changes towards the target environment temperature when it's not beneficial")
        .defineInRange("badTemperatureChangeSpeed", 1F, 0F, 50F);

    public static final ModConfigSpec.DoubleValue hotIngotTemperature = BUILDER
        .comment("How much do items in the forge:hot_ingots tag modify the temperature of the player")
        .defineInRange("hotIngotTemperature", 1F, 0F, Float.MAX_VALUE);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue healthDrainIconVerticalOffset = CLIENT_BUILDER
            .comment("Offset of the GUI element indicating health drain from temperature")
            .defineInRange("healthDrainIconVerticalOffset", 11F, -999F, +999F);

    public static final ModConfigSpec.DoubleValue hungerDrainIconVerticalOffset = CLIENT_BUILDER
            .comment("Offset of the GUI element indicating hunger drain from temperature")
            .defineInRange("hungerDrainIconVerticalOffset", 6F, -999F, +999F);

    public static final ModConfigSpec.DoubleValue thirstDrainIconVerticalOffset = CLIENT_BUILDER
            .comment("Offset of the GUI element indicating thirst drain from temperature")
            .defineInRange("thirstDrainIconVerticalOffset", 13F, -999F, +999F);

    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();
}
