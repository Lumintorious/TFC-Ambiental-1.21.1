package com.lumintorious.tfcambiental;

import net.dries007.tfc.util.Helpers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class TFCAmbientalTags {
    public static final TagKey<Item> SUNBLOCKING_APPAREL = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "sunblocking_apparel"));
    public static final TagKey<Item> HOT_INGOTS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath( "forge", "hot_ingots"));
    public static final TagKey<Block> WARM_STUFF = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "warm_stuff"));
    public static final TagKey<Block> HOT_STUFF = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "hot_stuff"));
    public static final TagKey<Block> COLD_STUFF = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "cold_stuff"));
    public static final TagKey<Fluid> SPRING_WATER = TagKey.create(Registries.FLUID, Helpers.identifier("spring_water"));
    public static final TagKey<EntityType<?>> HOT_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "hot_entities"));
    public static final TagKey<EntityType<?>> COLD_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "cold_entities"));
}
