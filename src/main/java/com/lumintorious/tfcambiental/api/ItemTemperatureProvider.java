package com.lumintorious.tfcambiental.api;

import com.lumintorious.tfcambiental.TFCAmbientalConfig;
import com.lumintorious.tfcambiental.TFCAmbientalTags;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@FunctionalInterface
public interface ItemTemperatureProvider
{
    Optional<TemperatureModifier> getModifier(Player player, ItemStack stack);

    static void evaluateAll(Player player, TemperatureModifier.Cache cache) {
        for (ItemStack stack : player.getInventory().items) {
            for (ItemTemperatureProvider provider : AmbientalRegistry.ITEMS) {
                cache.add(provider.getModifier(player, stack));
            }
        }
    }

    static Optional<TemperatureModifier> handleTemperatureCapability(Player player, ItemStack stack) {
        var cap = HeatCapability.get(stack);
        if (cap == null) {
            return Optional.empty();
        }
        float temp = cap.getTemperature() / 800;
        return Optional.of(new TemperatureModifier("heat_item", temp, 0.1f * stack.getCount()));
    }

    static Optional<TemperatureModifier> handleHotIngots(Player player, ItemStack stack) {
        return stack.is(TFCAmbientalTags.HOT_INGOTS) ? Optional.of(new TemperatureModifier("heat_item", TFCAmbientalConfig.hotIngotTemperature.get().floatValue(), 0.1f * stack.getCount())) : TemperatureModifier.none();
    }
}
