package com.lumintorious.tfcambiental.api;

import com.lumintorious.tfcambiental.TFCAmbientalTags;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class AmbientalRegistry<Type> implements Iterable<Type>
{
    public static final AmbientalRegistry<ItemTemperatureProvider> ITEMS = new AmbientalRegistry<>();
    public static final AmbientalRegistry<BlockTemperatureProvider> BLOCKS = new AmbientalRegistry<>();
    public static final AmbientalRegistry<BlockEntityTemperatureProvider> BLOCK_ENTITIES = new AmbientalRegistry<>();
    public static final AmbientalRegistry<EnvironmentalTemperatureProvider> ENVIRONMENT = new AmbientalRegistry<>();
    public static final AmbientalRegistry<EquipmentTemperatureProvider> EQUIPMENT = new AmbientalRegistry<>();
    public static final AmbientalRegistry<EntityTemperatureProvider> ENTITIES = new AmbientalRegistry<>();

    static {
        EQUIPMENT.register(EquipmentTemperatureProvider::handleSunlightCap);
        EQUIPMENT.register(EquipmentTemperatureProvider::handleClothes);

        ITEMS.register(ItemTemperatureProvider::handleTemperatureCapability);
        ITEMS.register(ItemTemperatureProvider::handleHotIngots);

        BLOCK_ENTITIES.register(BlockEntityTemperatureProvider::handleCharcoalForge);
        BLOCK_ENTITIES.register(BlockEntityTemperatureProvider::handleFirePit);
        BLOCK_ENTITIES.register(BlockEntityTemperatureProvider::handleBloomery);
        BLOCK_ENTITIES.register(BlockEntityTemperatureProvider::handleIHeatBlock);

        BLOCKS.register((player, pos, state) ->
                Optional.of(new TemperatureModifier("hot_block", 1f, 0f, -10f)).filter((mod) -> state.is(TFCAmbientalTags.HOT_STUFF))
        );
        BLOCKS.register((player, pos, state) ->
                Optional.of(new TemperatureModifier("cold_stuff", -0.5f, 0f)).filter((mod) -> state.is(TFCAmbientalTags.COLD_STUFF))
        );
        BLOCKS.register((player, pos, state) ->
            Optional.of(new TemperatureModifier("warm_block", 0.5f, 0f, -5f)).filter((mod) -> state.is(TFCAmbientalTags.WARM_STUFF))
        );

        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleGeneralTemperature);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleTimeOfDay);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleShade);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleCozy);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleThirst);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleFood);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleDiet);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleFire);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleWater);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleRain);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleSprinting);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleUnderground);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleWetness);
        ENVIRONMENT.register(EnvironmentalTemperatureProvider::handleTFCSurviveEffects);

        ENTITIES.register(EntityTemperatureProvider::handleHotEntities);
        ENTITIES.register(EntityTemperatureProvider::handleColdEntities);
    }

    private final ArrayList<Type> list = new ArrayList<>();

    private AmbientalRegistry() {
    }

    public void register(Type type) {
        list.add(type);
    }

    @Override
    public Iterator<Type> iterator() {
        return list.iterator();
    }
}
