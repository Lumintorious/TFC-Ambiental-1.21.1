package com.lumintorious.tfcambiental.api;

import com.lumintorious.tfcambiental.TFCAmbiental;
import com.lumintorious.tfcambiental.TFCAmbientalConfig;
import com.lumintorious.tfcambiental.TFCAmbientalTags;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import com.lumintorious.tfcambiental.item.ClothesItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@FunctionalInterface
public interface EquipmentTemperatureProvider
{
    Optional<TemperatureModifier> getModifier(Player player, ItemStack stack);

    static Optional<TemperatureModifier> handleClothes(Player player, ItemStack stack) {
        if (stack.getItem() instanceof ClothesItem clothesItem) {
            return clothesItem.getProvider().getModifier(player, stack);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleSunlightCap(Player player, ItemStack stack) {
        float AVERAGE = TFCAmbientalConfig.averageTemperature.get().floatValue();
        if (stack.getItem() instanceof ArmorItem armorItem && armorItem.getType() == ArmorItem.Type.HELMET) {
            if (player.level().getBrightness(LightLayer.SKY, player.getOnPos().above()) > 14) {
                float envTemp = EnvironmentalTemperatureProvider.getEnvironmentTemperatureWithTimeOfDay(player);
                if (envTemp > AVERAGE) {
                    float diff = envTemp - AVERAGE;
                    Optional<TemperatureModifier> helmetMod = handleClothes(player, stack);
                    if (helmetMod.isPresent()) {
                        diff -= helmetMod.get().getChange();
                    } else {
                        diff -= 1;
                    }
                    return TemperatureModifier.defined("sunlight_protection", diff * -0.20f, -0.1f);
                }
            }
        }
        return TemperatureModifier.none();
    }

    static void evaluateAll(Player player, TemperatureModifier.Cache cache) {
        CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(c -> {
            for (int i = 0; i < c.getSlots(); i++) {
                ItemStack stack = c.getStackInSlot(i);
                for (var fn : AmbientalRegistry.EQUIPMENT) {
                    cache.add(fn.getModifier(player, stack));
                }
            }
        });
        for (var fn : AmbientalRegistry.EQUIPMENT) {
            cache.add(fn.getModifier(player, player.getItemBySlot(EquipmentSlot.HEAD)));
            cache.add(fn.getModifier(player, player.getItemBySlot(EquipmentSlot.CHEST)));
            cache.add(fn.getModifier(player, player.getItemBySlot(EquipmentSlot.LEGS)));
            cache.add(fn.getModifier(player, player.getItemBySlot(EquipmentSlot.FEET)));
        }
    }

    static ItemStack getEquipmentByType(Player player, ArmorItem.Type type) {
        var feetArmor = player.getItemBySlot(EquipmentSlot.FEET);
        if (!feetArmor.isEmpty() && feetArmor.getItem() instanceof ArmorItem armorItem && armorItem.getType().equals(type)) {
            return feetArmor;
        }
        return CuriosApi.getCuriosHelper().getEquippedCurios(player).map(c -> {
            for (int i = 0; i < c.getSlots(); i++) {
                ItemStack stack = c.getStackInSlot(i);
                if (stack.getItem() instanceof ArmorItem armorItem && armorItem.getType().equals(type)) {
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }
}
