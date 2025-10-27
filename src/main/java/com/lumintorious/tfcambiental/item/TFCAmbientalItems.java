package com.lumintorious.tfcambiental.item;

import com.lumintorious.tfcambiental.TFCAmbiental;
import net.dries007.tfc.common.TFCArmorMaterials;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class TFCAmbientalItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TFCAmbiental.MODID);

//    public static final DeferredItem<Item> HOUSE_TESTER = ITEMS.register(
//            "house_tester",
//            () -> new HouseTester(new Item.Properties().stacksTo(1))
//    );
//
//    public static final DeferredItem<Item> SNOWSHOES = ITEMS.register(
//            "snowshoes",
//            () -> new Snowshoes(new Item.Properties().stacksTo(1).durability(27000))
//    );

    public static final DeferredItem<Item> LEATHER_APRON = ITEMS.register(
            "leather_apron",
            () -> new ClothesItem(ClothesMaterial.LEATHER_APRON_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1).durability(1))
    );

    public static final DeferredItem<Item> STRAW_HAT = ITEMS.register(
            "straw_hat",
            () -> new ArmorItem(ClothesMaterial.STRAW_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).durability(1))
    );

    public static final DeferredItem<Item> WOOL_HAT = ITEMS.register(
            "wool_hat",
            () -> new ClothesItem(ClothesMaterial.WOOL_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> WOOL_SWEATER = ITEMS.register(
            "wool_sweater",
            () -> new ClothesItem(ClothesMaterial.WOOL_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> WOOL_PANTS = ITEMS.register(
            "wool_pants",
            () -> new ClothesItem(ClothesMaterial.WOOL_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> WOOL_BOOTS = ITEMS.register(
            "wool_boots",
            () -> new ClothesItem(ClothesMaterial.WOOL_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1).durability(1))
    );

    public static final DeferredItem<Item> BURLAP_COWL = ITEMS.register(
            "burlap_cowl",
            () -> new ClothesItem(ClothesMaterial.BURLAP_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> BURLAP_SHIRT = ITEMS.register(
            "burlap_shirt",
            () -> new ClothesItem(ClothesMaterial.BURLAP_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> BURLAP_PANTS = ITEMS.register(
            "burlap_pants",
            () -> new ClothesItem(ClothesMaterial.BURLAP_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> BURLAP_SHOES = ITEMS.register(
            "burlap_shoes",
            () -> new ClothesItem(ClothesMaterial.BURLAP_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1).durability(1))
    );

    public static final DeferredItem<Item> LEATHER_HAT = ITEMS.register(
            "insulated_leather_hat",
            () -> new ClothesItem(ClothesMaterial.INSULATED_LEATHER, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> LEATHER_TUNIC = ITEMS.register(
            "insulated_leather_tunic",
            () -> new ClothesItem(ClothesMaterial.INSULATED_LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> LEATHER_PANTS = ITEMS.register(
            "insulated_leather_pants",
            () -> new ClothesItem(ClothesMaterial.INSULATED_LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1).durability(1))
    );
    public static final DeferredItem<Item> LEATHER_BOOTS = ITEMS.register(
            "insulated_leather_boots",
            () -> new ClothesItem(ClothesMaterial.INSULATED_LEATHER, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1).durability(1))
    );
}
