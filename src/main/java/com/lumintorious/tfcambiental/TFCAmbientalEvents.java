package com.lumintorious.tfcambiental;

import com.lumintorious.tfcambiental.data.PlayerTemperaturePacket;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import com.lumintorious.tfcambiental.item.ClothesItem;
import com.lumintorious.tfcambiental.item.TFCAmbientalItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.text.DecimalFormat;
import java.util.Optional;

public class TFCAmbientalEvents {
    public static final String CURIOS_ID = "curios";

    public static void initAll(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(TFCAmbientalEvents::onPlayerUpdate);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, TFCAmbientalEvents::addTooltips);
        modEventBus.addListener(TFCAmbientalEvents::onInterModComms);
        modEventBus.addListener(TFCAmbientalEvents::registerPayloadHandlers);
    }

    public static void onPlayerUpdate(PlayerTickEvent.Post event) {
        var level = event.getEntity().level();
        if (event.getEntity() == null || event.getEntity().level() == null) {
            return;
        }
        if (!level.isClientSide()) {
            event.getEntity().getData(TFCAmbiental.PLAYER_TEMPERATURE).update(event.getEntity());
            if (event.getEntity().isHolding(Items.ACACIA_BOAT)) {
                event.getEntity().sendSystemMessage(Component.literal("===="));
                event.getEntity().sendSystemMessage(Component.literal("" + event.getEntity().getData(TFCAmbiental.PLAYER_TEMPERATURE).getTemperatureChange()));
                for (TemperatureModifier modifier : event.getEntity().getData(TFCAmbiental.PLAYER_TEMPERATURE).getModifiers()) {
                    event.getEntity().sendSystemMessage(Component.literal(modifier.getUnlocalizedName() + " - " + modifier.getChange()));
                }
            }
        } else {
            // Drip when wet
//            if (level.getRandom().nextInt() <= TFCAmbientalConfig.CLIENT.drippiness.get() && !event.getEntity().isUnderWater()) {
//                event.getEntity().getCapability(TemperatureCapability.CAPABILITY).ifPresent(temperatureCapability -> {
//                    if (temperatureCapability.getWetness() > 0.5f && level.getRandom().nextDouble() < (0.005D * temperatureCapability.getWetness())) {
//                        var pos = event.getEntity().position();
//                        level.addParticle(ParticleTypes.FALLING_WATER, Mth.lerp(level.random.nextDouble(), pos.x - 0.3D, pos.x + 0.3D), pos.y + 1.7D, Mth.lerp(level.random.nextDouble(), pos.z - 0.3D, pos.z + 0.3D), 0.0D, 0.0D, 0.0D);
//                    }
//                });
//            }
        }
    }

    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
//            for (RegistryObject<Item> item : TFCAmbientalItems.ITEMS.getEntries()) {
//                if (!item.getId().getPath().equals("snowshoes")) {
//                    event.accept(item);
//                }
//            }
        }
    }

    private static void onInterModComms(InterModEnqueueEvent event) {
        ResourceLocation HEAD_SLOT = ResourceLocation.fromNamespaceAndPath("curios", "slot/clothes_hat");
        ResourceLocation CHEST_SLOT = ResourceLocation.fromNamespaceAndPath("curios", "slot/clothes_torso");
        ResourceLocation LEGS_SLOT = ResourceLocation.fromNamespaceAndPath("curios", "slot/clothes_pants");
        ResourceLocation FEET_SLOT = ResourceLocation.fromNamespaceAndPath("curios", "slot/clothes_socks");
        InterModComms.sendTo(CURIOS_ID, SlotTypeMessage.REGISTER_TYPE, () ->
                new SlotTypeMessage.Builder("clothes_hat")
                        .icon(HEAD_SLOT)
                        .priority(90)
                        .build()
        );
        InterModComms.sendTo(CURIOS_ID, SlotTypeMessage.REGISTER_TYPE, () ->
                new SlotTypeMessage.Builder("clothes_torso")
                        .icon(CHEST_SLOT)
                        .priority(91)
                        .build()
        );
        InterModComms.sendTo(CURIOS_ID, SlotTypeMessage.REGISTER_TYPE, () ->
                new SlotTypeMessage.Builder("clothes_pants")
                        .icon(LEGS_SLOT)
                        .priority(92)
                        .build()
        );
        InterModComms.sendTo(CURIOS_ID, SlotTypeMessage.REGISTER_TYPE, () ->
                new SlotTypeMessage.Builder("clothes_socks")
                        .icon(FEET_SLOT)
                        .priority(93)
                        .build()
        );
        InterModComms.sendTo(CURIOS_ID, SlotTypeMessage.REGISTER_TYPE, () ->
                new SlotTypeMessage.Builder("feet")
                        .icon(FEET_SLOT)
                        .priority(94)
                        .build()
        );
    }

    private static String formatAttribute(float attribute) {
        var attributeD = new DecimalFormat("0.0");
        var attributeS = attributeD.format(attribute);
        if (attribute > 0.001 || attribute < -0.001) {
            if (attribute > 0) {
                return "+" + (attributeS);
            } else {
                return (attributeS);
            }
        } else {
            return "";
        }
    }

    private static void addTooltips(ItemTooltipEvent event) {
        if (event.getEntity() != null && !event.getEntity().level().isClientSide()) return;
        float warmth = 0;
        float insulation = 0;
        warmth = ((float) Math.floor(warmth / 0.25f)) * 0.25f;
        insulation = ((float) Math.floor(insulation / 0.25f)) * -0.25f;

        if (event.getItemStack().getItem() instanceof ClothesItem clothesItem) {
            Optional<TemperatureModifier> modifier = clothesItem.getProvider().getModifier(event.getEntity(), event.getItemStack());
            if (modifier.isPresent()) {
                warmth = (modifier.get().getChange());
                insulation = (modifier.get().getPotency() * -100);
                if (warmth != 0) {
                    event.getToolTip().add(Component.translatable("tfcambiental.tooltip.warmth", formatAttribute(warmth)).withStyle(ChatFormatting.BLUE));
                }
                if (clothesItem.getType().equals(ArmorItem.Type.HELMET)) {
                    event.getToolTip().add(Component.translatable("tfcambiental.tooltip.sun_protection").withStyle(ChatFormatting.BLUE));
                }
                if (insulation != 0) {
                    event.getToolTip().add(Component.translatable("tfcambiental.tooltip.insulation", (formatAttribute(insulation)) + "%").withStyle(ChatFormatting.BLUE));
                }
            }
            if (event.getItemStack().is(TFCAmbientalItems.LEATHER_APRON.get())) {
                event.getToolTip().add(Component.translatable("tfcambiental.tooltip.apron").withStyle(ChatFormatting.BLUE));
            }
        } else {
            if (event.getItemStack().getItem() instanceof ArmorItem armorItem && armorItem.getType().equals(ArmorItem.Type.HELMET)) {
                event.getToolTip().add(Component.translatable("tfcambiental.tooltip.sun_protection").withStyle(ChatFormatting.BLUE));
            }
        }
    }


    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                PlayerTemperaturePacket.TYPE,
                PlayerTemperaturePacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        TFCAmbientalEvents::handleTemperatureUpdateOnClient,
                        TFCAmbientalEvents::handleTemperatureUpdateOnServer
                )
        );
    }

    public static void handleTemperatureUpdateOnClient(final PlayerTemperaturePacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                var newData = data.create();
                Minecraft.getInstance().player.setData(TFCAmbiental.PLAYER_TEMPERATURE, newData);

            }
        });
    }

    public static void handleTemperatureUpdateOnServer(final PlayerTemperaturePacket data, final IPayloadContext context) {

    }
}
