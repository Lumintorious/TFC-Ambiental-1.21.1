package com.lumintorious.tfcambiental.api;

import com.lumintorious.tfcambiental.TFCAmbiental;
import com.lumintorious.tfcambiental.TFCAmbientalConfig;
import com.lumintorious.tfcambiental.TFCAmbientalTags;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.component.food.NutritionData;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;

import java.util.Optional;
import java.util.Set;

@FunctionalInterface
public interface EnvironmentalTemperatureProvider
{
    Optional<TemperatureModifier> getModifier(Player player);

    static boolean calculateEnclosure(Player player, int radius) {
        PathNavigationRegion region = new PathNavigationRegion(
                player.level(),
                player.getOnPos().above().offset(-radius, -radius, -radius),
                player.getOnPos().above().offset(radius, 400, radius)
        );
        Bat guineaPig = new Bat(EntityType.BAT, player.level());
        guineaPig.setPos(player.getPosition(0).add(0, 1, 0));
        guineaPig.setBaby(true);
        FlyNodeEvaluator evaluator = new FlyNodeEvaluator();
        PathFinder finder = new PathFinder(evaluator, 500);
        Path path = finder.findPath(
                region,
                guineaPig,
                Set.of(player.getOnPos().above().atY(258)),
                500,
                0,
                12
        );
        return path == null || path.getNodeCount() < 255 - player.getOnPos().above().getY();
    }

    static float getEnvironmentTemperatureWithTimeOfDay(Player player) {
        return getEnvironmentTemperature(player) + handleTimeOfDay(player).get().getChange();
    }

    static float getEnvironmentTemperature(Player player) {
        float actual = Climate.getTemperature(player.level(), player.getOnPos()) + 4;
        float diff = actual - TFCAmbientalConfig.averageTemperature.get();
        return TFCAmbientalConfig.averageTemperature.get() + (diff + Math.signum(diff) * 4 * (Math.min(4, Math.abs(diff)) / 4));
    }

    static float getEnvironmentHumidity(Player player) {
        return Climate.getRainfall(player.level(), player.getOnPos()) / 3000;
    }

    static int getSkylight(Player player) {
        BlockPos pos = new BlockPos(player.getOnPos()).above(2);
        return player.level().getBrightness(LightLayer.SKY, pos);
    }

    static int getBlockLight(Player player) {
        BlockPos pos = new BlockPos(player.getOnPos()).above(1);
        return player.level().getBrightness(LightLayer.BLOCK, pos);
    }

    static Optional<TemperatureModifier> handleFire(Player player) {
        if (player.isOnFire()) {
            TemperatureModifier.defined("on_fire", 4f, 4f, -1f);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleGeneralTemperature(Player player) {
        return Optional.of(new TemperatureModifier("environment", getEnvironmentTemperature(player), getEnvironmentHumidity(player)));
    }

    static Optional<TemperatureModifier> handleTimeOfDay(Player player) {
        int dayTicks = (int) (player.level().dayTime() % 24000);
        if (dayTicks < 6000) {
            return TemperatureModifier.defined("morning", 1f, 0);
        } else if (dayTicks < 12000) {
            return TemperatureModifier.defined("afternoon", 4f, 0, -0.02f);
        } else if (dayTicks < 18000) {
            return TemperatureModifier.defined("evening", 1f, 0);
        } else {
            return TemperatureModifier.defined("night", -1f, 0);
        }
    }

    static Optional<TemperatureModifier> handleWater(Player player) {
        if (player.isInWater()) {
            BlockPos pos = player.getOnPos().above();
            BlockState state = player.level().getBlockState(pos);
            if (state.getFluidState().is(TFCAmbientalTags.SPRING_WATER)) {
                return TemperatureModifier.defined("in_hot_water", 5f, 6f, 10f);
            } else if (state.getBlock() == Blocks.LAVA) {
                return TemperatureModifier.defined("in_lava", 10f, 5f, -10f);
            }
            return TemperatureModifier.defined("in_water", -5f, 6f, 10f);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleRain(Player player) {
        if (player.level().isRaining()) {
            var isInRain = player.level().isRainingAt(player.blockPosition());
            if (getSkylight(player) < 15) {
                return TemperatureModifier.defined("weather", -2f, 0.1f, isInRain ? 0.5f : 0);
            }
            return TemperatureModifier.defined("weather", -4f, 0.3f, isInRain ? 4f : 0.5f);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleSprinting(Player player) {
        if (player.isSprinting()) {
            return TemperatureModifier.defined("sprint", 2f, 0.3f, -0.05f);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleUnderground(Player player) {
        if (getSkylight(player) < 2 && player.getOnPos().getY() < 60) {
            return TemperatureModifier.defined("underground", (float) (-0.1 * (60 - player.getOnPos().getY())), 0.2f);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleShade(Player player) {
        int light = Math.max(12, getSkylight(player));
        if (light < 15) {
            player.getData(TFCAmbiental.PLAYER_TEMPERATURE).setUnderRoof(true);
            float temp = getEnvironmentTemperatureWithTimeOfDay(player);
            float avg = TFCAmbientalConfig.averageTemperature.get().floatValue();
            if (temp > avg) {
                return TemperatureModifier.defined("shade", -Math.abs(avg - temp) * 0.6f, 0f);
            }
        } else {
            player.getData(TFCAmbiental.PLAYER_TEMPERATURE).setUnderRoof(false);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleCozy(Player player) {
        float temp = getEnvironmentTemperatureWithTimeOfDay(player);
        float avg = TFCAmbientalConfig.averageTemperature.get().floatValue();

        var temperatureCapability = player.getData(TFCAmbiental.PLAYER_TEMPERATURE);
        temperatureCapability.setInside(EnvironmentalTemperatureProvider.calculateEnclosure(player, 30));
        if (temp < avg - 1) {
            final boolean[] isInside = {false};
            isInside[0] = temperatureCapability.isInside();

            if (isInside[0]) {
                return TemperatureModifier.defined("cozy", Math.abs(avg - 1 - temp) * 0.6f, 0f);
            }
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleThirst(Player player) {
        if (IPlayerInfo.get(player) instanceof PlayerInfo stats) {
            if (stats.getThirst() > 70f && getEnvironmentTemperatureWithTimeOfDay(player) > TFCAmbientalConfig.averageTemperature.get().floatValue() + 3) {
                return TemperatureModifier.defined("well_hydrated", -1.5f, 0f);
            }
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleFood(Player player) {
        if (player.getFoodData().getFoodLevel() > 14 && getEnvironmentTemperatureWithTimeOfDay(player) < TFCAmbientalConfig.averageTemperature.get().floatValue() - 3) {
            return TemperatureModifier.defined("well_fed", 1.5f, 0f);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleDiet(Player player) {
        if (IPlayerInfo.get(player) instanceof PlayerInfo stats) {
            if (getEnvironmentTemperatureWithTimeOfDay(player) < TFCAmbientalConfig.coolThreshold.get().floatValue()) {
                float grainLevel = stats.nutrition().getNutrient(Nutrient.GRAIN);
                float meatLevel = stats.nutrition().getNutrient(Nutrient.PROTEIN);
                return TemperatureModifier.defined("nutrients", 4f * grainLevel * meatLevel, 0f);
            }
            if (getEnvironmentTemperatureWithTimeOfDay(player) > TFCAmbientalConfig.hotThreshold.get().floatValue()) {
                float fruitLevel = stats.nutrition().getNutrient(Nutrient.FRUIT);
                float veggieLevel = stats.nutrition().getNutrient(Nutrient.VEGETABLES);
                return TemperatureModifier.defined("nutrients", -4f * fruitLevel * veggieLevel, 0f);
            }
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleWetness(Player player) {
        var temperatureCapability = player.getData(TFCAmbiental.PLAYER_TEMPERATURE);
        // TODO Wool clothing halves the effect of wetness
        var mod = -0.01f;
        var potency = 0.2f;
        // If you're wet in a cold environment, the temperature drop is significantly higher
        if (temperatureCapability.getWetness() > 1.5f && !player.isInWater()) {
            var envTemperature = getEnvironmentTemperature(player);
            potency = envTemperature < temperatureCapability.getTemperature() ? 5.5f : potency;
        }
        return TemperatureModifier.defined("wetness", mod * temperatureCapability.getWetness(), potency, !player.isInWater() ? -0.03f : 0);
    }

    static void evaluateAll(Player player, TemperatureModifier.Cache cache) {
        for (var fn : AmbientalRegistry.ENVIRONMENT) {
            cache.add(fn.getModifier(player));
        }
    }
}
