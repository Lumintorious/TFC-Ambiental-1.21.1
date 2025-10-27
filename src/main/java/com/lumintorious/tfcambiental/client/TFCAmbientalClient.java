package com.lumintorious.tfcambiental.client;

import com.lumintorious.tfcambiental.TFCAmbiental;
import com.lumintorious.tfcambiental.TFCAmbientalConfig;
import com.lumintorious.tfcambiental.compat.curios.ClothesCurioRenderer;
import com.lumintorious.tfcambiental.item.TFCAmbientalItems;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = TFCAmbiental.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = TFCAmbiental.MODID, value = Dist.CLIENT)
public class TFCAmbientalClient {
    public TFCAmbientalClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(TFCAmbientalClient::onRenderGameOverlayPost);
//        CuriosRendererRegistry.register(TFCAmbientalItems.SNOWSHOES.get(), SnowshoesCurioRenderer::new);

        CuriosRendererRegistry.register(TFCAmbientalItems.STRAW_HAT.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.LEATHER_APRON.get(), ClothesCurioRenderer::new);

        CuriosRendererRegistry.register(TFCAmbientalItems.WOOL_HAT.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.WOOL_SWEATER.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.WOOL_PANTS.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.WOOL_BOOTS.get(), ClothesCurioRenderer::new);

        CuriosRendererRegistry.register(TFCAmbientalItems.BURLAP_COWL.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.BURLAP_SHIRT.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.BURLAP_PANTS.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.BURLAP_SHOES.get(), ClothesCurioRenderer::new);

        CuriosRendererRegistry.register(TFCAmbientalItems.LEATHER_HAT.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.LEATHER_TUNIC.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.LEATHER_PANTS.get(), ClothesCurioRenderer::new);
        CuriosRendererRegistry.register(TFCAmbientalItems.LEATHER_BOOTS.get(), ClothesCurioRenderer::new);
    }

    public static void onRenderGameOverlayPost(RenderGuiLayerEvent.Post event) {
        // todo this should probably be a forge ingame gui
        final GuiGraphics graphics = event.getGuiGraphics();
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;
        if (player != null) {
            if (event.getName() == VanillaGuiLayers.BOSS_OVERLAY) {
                TFCAmbientalGuiRenderer.INSTANCE.render(event.getGuiGraphics(), event.getPartialTick());
            }
        }
    }
}
