package com.lumintorious.tfcambiental.api;

import com.lumintorious.tfcambiental.TFCAmbientalConfig;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import com.lumintorious.tfcambiental.item.TFCAmbientalItems;
import net.dries007.tfc.common.blockentities.*;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Locale;
import java.util.Optional;

@FunctionalInterface
public interface BlockEntityTemperatureProvider
{
    Optional<TemperatureModifier> getModifier(Player player, BlockEntity entity);

    static void evaluateAll(Player player, TemperatureModifier.Cache cache) {
//        BlockTemperatureProvider.evaluateAll(player, cache);
    // Already handled in BlockTemperatureProvider
    }

    private static boolean hasProtection(Player player) {
        var item = CuriosApi.getCuriosHelper().findCurios(player, TFCAmbientalItems.LEATHER_APRON.get());
        if (item.isEmpty()) {
            return false;
        }
        float environmentTemperature = EnvironmentalTemperatureProvider.getEnvironmentTemperatureWithTimeOfDay(player);
        float AVERAGE = TFCAmbientalConfig.averageTemperature.get().floatValue();
        return environmentTemperature > AVERAGE;
    }

    static Optional<TemperatureModifier> handleCharcoalForge(Player player, BlockEntity entity) {
        if (entity instanceof CharcoalForgeBlockEntity forge) {

            float temp = forge.getTemperature();
            float change = temp / 120f;
            if (hasProtection(player)) {
                change = change * 0.05f;
            }
            return TemperatureModifier.defined("charcoal_forge", change, 0);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleFirePit(Player player, BlockEntity entity) {
        if (entity instanceof AbstractFirepitBlockEntity<?> pit) {
            float temp = pit.getTemperature();
            float change = temp / 120f;
            if (hasProtection(player)) {
                change = change * 0.3f;
            }
            return TemperatureModifier.defined("fire_pit", change, 0);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleBloomery(Player player, BlockEntity entity) {
        if (entity instanceof BloomeryBlockEntity bloomery) {
            float change = bloomery.getRemainingTicks() > 0 ? 8f : 0f;
            if (hasProtection(player)) {
                change = change * 0.3f;
            }
            return TemperatureModifier.defined("bloomery", change, 0);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleIHeatBlock(Player player, BlockEntity entity) {
//        if (entity instanceof AbstractFirepitBlockEntity<?> firepit) {
//            return Optional.of(new TemperatureModifier(entity.getClass().getName().toLowerCase(Locale.ROOT), firepit.getTemperature() / 140f, 0));
//        }
//        if (entity instanceof CharcoalForgeBlockEntity forge) {
//            return Optional.of(new TemperatureModifier(entity.getClass().getName().toLowerCase(Locale.ROOT), forge.getTemperature() / 140f, 0));
//        }
//        if (entity instanceof BloomeryBlockEntity) {
//            return Optional.of(new TemperatureModifier(entity.getClass().getName().toLowerCase(Locale.ROOT), 5, 0));
//        }
        return Optional.empty();
    }
}
