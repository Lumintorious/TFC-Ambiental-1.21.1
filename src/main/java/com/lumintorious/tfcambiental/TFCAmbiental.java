package com.lumintorious.tfcambiental;

import com.lumintorious.tfcambiental.data.PlayerTemperature;
import com.lumintorious.tfcambiental.item.ClothesMaterial;
import com.lumintorious.tfcambiental.item.TFCAmbientalItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TFCAmbiental.MODID)
public class TFCAmbiental {
    public static final String MODID = "tfcambiental";
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerTemperature>> PLAYER_TEMPERATURE = ATTACHMENT_TYPES.register("player_temperature", () -> {
        return AttachmentType.builder((player) -> new PlayerTemperature())
                .serialize(PlayerTemperature.SERIALIZER)
                .build();
    });


    public static final ResourceKey<DamageType> HOT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "heatstroke"));
    public static final ResourceKey<DamageType> FREEZE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "frostbite"));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("tfcambiental", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tfcambiental")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(TFCAmbientalItems.STRAW_HAT::toStack)
            .displayItems((parameters, output) -> {
                output.accept(TFCAmbientalItems.STRAW_HAT);
                output.accept(TFCAmbientalItems.WOOL_HAT);
                output.accept(TFCAmbientalItems.WOOL_SWEATER);
                output.accept(TFCAmbientalItems.WOOL_PANTS);
                output.accept(TFCAmbientalItems.WOOL_BOOTS);
                output.accept(TFCAmbientalItems.BURLAP_COWL);
                output.accept(TFCAmbientalItems.BURLAP_SHIRT);
                output.accept(TFCAmbientalItems.BURLAP_PANTS);
                output.accept(TFCAmbientalItems.BURLAP_SHOES);
                output.accept(TFCAmbientalItems.LEATHER_HAT);
                output.accept(TFCAmbientalItems.LEATHER_TUNIC);
                output.accept(TFCAmbientalItems.LEATHER_PANTS);
                output.accept(TFCAmbientalItems.LEATHER_APRON);
                output.accept(TFCAmbientalItems.LEATHER_BOOTS);
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public TFCAmbiental(IEventBus modEventBus, ModContainer modContainer) {
        ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
//        BLOCKS.register(modEventBus);
        ClothesMaterial.CLOTHES_MATERIALS.register(modEventBus);
        TFCAmbientalItems.ITEMS.register(modEventBus);
        TFCAmbientalEvents.initAll(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, TFCAmbientalConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, TFCAmbientalConfig.CLIENT_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }
}
