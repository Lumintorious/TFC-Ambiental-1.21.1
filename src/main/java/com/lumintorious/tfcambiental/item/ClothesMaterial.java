package com.lumintorious.tfcambiental.item;

import com.lumintorious.tfcambiental.TFCAmbiental;
import com.lumintorious.tfcambiental.api.EquipmentTemperatureProvider;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClothesMaterial {

    public static final DeferredRegister<ArmorMaterial> CLOTHES_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, TFCAmbiental.MODID);

    public final static Holder<ArmorMaterial> STRAW_MATERIAL = CLOTHES_MATERIALS.register("straw", () ->
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 2);
                        map.put(ArmorItem.Type.CHESTPLATE, 2);
                        map.put(ArmorItem.Type.HELMET, 1);
                        map.put(ArmorItem.Type.BODY, 4);
                    }),
                    0,
                    Holder.direct(SoundEvents.CROP_BREAK),
                    () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "straw"), "", true), new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "straw"), "", false)),
                    1,
                    0
            )
    );

    public final static Holder<ArmorMaterial> BURLAP_MATERIAL = CLOTHES_MATERIALS.register("burlap_cloth", () ->
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 2);
                        map.put(ArmorItem.Type.CHESTPLATE, 2);
                        map.put(ArmorItem.Type.HELMET, 1);
                        map.put(ArmorItem.Type.BODY, 4);
                    }),
                    0,
                    Holder.direct(SoundEvents.WOOL_PLACE),
                    () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "burlap_cloth"), "", true), new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "burlap_cloth"), "", false)),
                    1,
                    0
            )
    );

    public final static Holder<ArmorMaterial> WOOL_MATERIAL = CLOTHES_MATERIALS.register("wool_cloth", () ->
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 2);
                        map.put(ArmorItem.Type.CHESTPLATE, 2);
                        map.put(ArmorItem.Type.HELMET, 1);
                        map.put(ArmorItem.Type.BODY, 4);
                    }),
                    0,
                    Holder.direct(SoundEvents.WOOL_PLACE),
                    () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "wool_cloth"), "", true), new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "wool_cloth"), "", false)),
                    1,
                    0
            )
    );

    public final static Holder<ArmorMaterial> INSULATED_LEATHER = CLOTHES_MATERIALS.register("insulated_leather", () ->
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 2);
                        map.put(ArmorItem.Type.CHESTPLATE, 2);
                        map.put(ArmorItem.Type.HELMET, 1);
                        map.put(ArmorItem.Type.BODY, 4);
                    }),
                    0,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "insulated_leather"), "", true), new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "insulated_leather"), "", false)),
                    1,
                    0
            )
    );

    public final static Holder<ArmorMaterial> LEATHER_APRON_MATERIAL = CLOTHES_MATERIALS.register("leather_apron", () ->
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 2);
                        map.put(ArmorItem.Type.CHESTPLATE, 2);
                        map.put(ArmorItem.Type.HELMET, 1);
                        map.put(ArmorItem.Type.BODY, 4);
                    }),
                    0,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "leather_apron"), "", true), new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "leather_apron"), "", false)),
                    1,
                    0
            )
    );

    public static EquipmentTemperatureProvider temperatureOf(ArmorMaterial material) {
        Map<ArmorMaterial, EquipmentTemperatureProvider> mappings = Map.of(
            BURLAP_MATERIAL.value(), (player, stack) -> Optional.of(new TemperatureModifier("clothes", -1f, -0.15f)),
            WOOL_MATERIAL.value(), (player, stack) -> Optional.of(new TemperatureModifier("clothes", 2f, -0.15f)),
            INSULATED_LEATHER.value(), (player, stack) -> Optional.of(new TemperatureModifier("clothes", 0.5f, -0.2f)),
            STRAW_MATERIAL.value(), (player, stack) -> Optional.of(new TemperatureModifier("clothes", -0.5f, -0.15f)),
            LEATHER_APRON_MATERIAL.value(), (player, stack) -> Optional.of(new TemperatureModifier("clothes", 0f, -0.1f))
        );

        return mappings.get(material);
    }
}
