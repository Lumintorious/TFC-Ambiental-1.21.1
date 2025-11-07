package com.lumintorious.tfcambiental.client;

import com.lumintorious.tfcambiental.TFCAmbiental;
import com.lumintorious.tfcambiental.TFCAmbientalConfig;
import com.lumintorious.tfcambiental.data.PlayerTemperature;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.lwjgl.opengl.GL11;

public class TFCAmbientalGuiRenderer implements LayeredDraw.Layer
{
    public static final TFCAmbientalGuiRenderer INSTANCE = new TFCAmbientalGuiRenderer();

    public static final ResourceLocation COLD_VIGNETTE = ResourceLocation.fromNamespaceAndPath("tfcambiental", "textures/gui/cold_vignette.png");
    public static final ResourceLocation HOT_VIGNETTE = ResourceLocation.fromNamespaceAndPath("tfcambiental","textures/gui/hot_vignette.png");
    public static final ResourceLocation MINUS = ResourceLocation.fromNamespaceAndPath("tfcambiental", "textures/gui/lower.png");
    public static final ResourceLocation PLUS = ResourceLocation.fromNamespaceAndPath("tfcambiental", "textures/gui/higher.png");
    public static final ResourceLocation MINUSER = ResourceLocation.fromNamespaceAndPath("tfcambiental", "textures/gui/lowerer.png");
    public static final ResourceLocation PLUSER = ResourceLocation.fromNamespaceAndPath("tfcambiental", "textures/gui/higherer.png");
    public static final ResourceLocation WET = ResourceLocation.fromNamespaceAndPath("tfcambiental", "textures/gui/wet.png");

    public static final ResourceLocation SHELTER_ROOF = ResourceLocation.fromNamespaceAndPath("tfcambiental", "textures/gui/shelter_roof.png");
    public static final ResourceLocation SHELTER_FULL = ResourceLocation.fromNamespaceAndPath("tfcambiental", "textures/gui/shelter_full.png");

    @Override
    public void render(GuiGraphics stack, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getCameraEntity() instanceof Player player) {
            if (player.isCreative() || !player.isAlive() || player.isSpectator()) {
                return;
            }
            PlayerTemperature tempSystem = player.getData(TFCAmbiental.PLAYER_TEMPERATURE);
            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();
            float redCol, greenCol, blueCol;

            int healthRowHeight = mc.getWindow().getGuiScaledHeight();
            int armorRowHeight = healthRowHeight - 51;
            int mid = mc.getWindow().getGuiScaledWidth() / 2;

            RenderSystem.enableBlend();
            float AVERAGE = TFCAmbientalConfig.averageTemperature.get().floatValue();
            float HOT_THRESHOLD = TFCAmbientalConfig.hotThreshold.get().floatValue();
            float COOL_THRESHOLD = TFCAmbientalConfig.coolThreshold.get().floatValue();
            if (tempSystem.getTemperature() > AVERAGE) {
                float hotRange = HOT_THRESHOLD - AVERAGE + 2;
                float red = Math.max(0, Math.min(1, (tempSystem.getTemperature() - AVERAGE) / hotRange));
                redCol = 1F;
                greenCol = 1.0F - red / 2.4F;
                blueCol = 1.0F - red / 1.6F;
            } else {
                float coolRange = AVERAGE - COOL_THRESHOLD - 2;
                float blue = Math.max(0, Math.min(1, (AVERAGE - tempSystem.getTemperature()) / coolRange));
                redCol = 1.0F - blue / 1.6F;
                greenCol = 1.0F - blue / 2.4F;
                blueCol = 1.0F;
            }

            float change = tempSystem.getTemperatureChange();

            if (tempSystem.isInside()) {
                drawTexturedModalRect(stack, mid - 9 + TFCAmbientalConfig.shelterX.get().floatValue(), armorRowHeight - 8 + TFCAmbientalConfig.shelterY.get().floatValue(), 18, 18, SHELTER_FULL);
            } else if (tempSystem.isUnderRoof()) {
                drawTexturedModalRect(stack, mid - 9 + TFCAmbientalConfig.shelterX.get().floatValue(), armorRowHeight - 8 + TFCAmbientalConfig.shelterY.get().floatValue(), 18, 18, SHELTER_ROOF);
            }
            RenderSystem.setShaderColor(redCol, greenCol, blueCol, 0.9F);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(redCol, greenCol, blueCol, 0.9F);

            // Hunger and Thirst loss indicators
            if (tempSystem.getTemperature() > TFCAmbientalConfig.hotThreshold.get()) {
                drawTexturedModalRect(stack, mid + 85 + TFCAmbientalConfig.thirstDrainX.get().floatValue(), armorRowHeight + 12 + TFCAmbientalConfig.thirstDrainY.get().floatValue(), 14, 14, MINUSER);
            }

            if (tempSystem.getTemperature() < TFCAmbientalConfig.coolThreshold.get()) {
                drawTexturedModalRect(stack, mid + 85 + TFCAmbientalConfig.hungerDrainX.get().floatValue(), armorRowHeight + 5 + TFCAmbientalConfig.hungerDrainY.get().floatValue(), 14, 14, MINUSER);
            }

            if (tempSystem.getTemperature() < TFCAmbientalConfig.freezeThreshold.get() || tempSystem.getTemperature() > TFCAmbientalConfig.burnThreshold.get()) {
                RenderSystem.setShaderColor(redCol, greenCol, blueCol, 0.9F);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(redCol, greenCol, blueCol, 0.9F);
                drawTexturedModalRect(stack, mid - 98 + TFCAmbientalConfig.healthDrainX.get().floatValue(), armorRowHeight + 10 + TFCAmbientalConfig.healthDrainY.get().floatValue(), 14, 14, MINUSER);
            }

            RenderSystem.setShaderColor(redCol, greenCol, blueCol, 0.9F);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(redCol, greenCol, blueCol, 0.9F);

            if (change > 0) {
                if (change > PlayerTemperature.HIGH_CHANGE) {
                    drawTexturedModalRect(stack, mid - 8 + TFCAmbientalConfig.temperatureX.get().floatValue(), armorRowHeight - 5  + TFCAmbientalConfig.temperatureY.get().floatValue(), 16, 16, PLUSER);
                } else {
                    drawTexturedModalRect(stack, mid - 8 + TFCAmbientalConfig.temperatureX.get().floatValue(), armorRowHeight - 5 + TFCAmbientalConfig.temperatureY.get().floatValue(), 16, 16, PLUS);
                }
            } else {
                if (change < -PlayerTemperature.HIGH_CHANGE) {
                    drawTexturedModalRect(stack, mid - 8 + TFCAmbientalConfig.temperatureX.get().floatValue(), armorRowHeight - 5 + TFCAmbientalConfig.temperatureY.get().floatValue(), 16, 16, MINUSER);
                } else {
                    drawTexturedModalRect(stack, mid - 8 + TFCAmbientalConfig.temperatureX.get().floatValue(), armorRowHeight - 5 + TFCAmbientalConfig.temperatureY.get().floatValue(), 16, 16, MINUS);
                }
            }
            if (tempSystem.getWetness() > 1) {
                drawTexturedModalRect(stack, (float) (mid - 12 + TFCAmbientalConfig.wetnessX.get()), (float) (armorRowHeight + 5 + TFCAmbientalConfig.wetnessY.get()), 16, 16, WET);
            }

            if (player.isCrouching()) {
                var shiftHeight = 0.0f;
                int air = player.getAirSupply();
                if (player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) || air < 300) {
                    shiftHeight = 10.0f;
                }

                Font f = Minecraft.getInstance().font;
                String tempStr = String.format("%.1f\u00BA -> %.1f\u00BA", tempSystem.getTemperature(), tempSystem.getTargetTemperature());
                stack.drawString(f, tempStr, mid + 94 + TFCAmbientalConfig.textStatusX.get().floatValue(), armorRowHeight + 40 + TFCAmbientalConfig.textStatusY.get().floatValue(), TFCAmbientalGuiRenderer.getIntFromColor(redCol, greenCol, blueCol), false);

//                String wetStr = String.format("%.1f", tempSystem.getPotency());
//                stack.drawString(f, wetStr, mid - 16 + f.width(wetStr) / 2F, armorRowHeight + 1 - shiftHeight, TFCAmbientalGuiRenderer.getIntFromColor(redCol, greenCol, blueCol), false);

            }

            drawTemperatureVignettes(stack, width, height, player);

            RenderSystem.setShaderColor(1f, 1f, 1f, 0.9F);
            RenderSystem.disableBlend();
        }
    }

    private void drawTexturedModalRect(GuiGraphics stack, float x, float y, float width, float height, ResourceLocation loc) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindForSetup(loc);
        RenderSystem.setShaderTexture(0, loc);

        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        stack.blit(loc, (int) x, (int) y, 0, 0, (int) width, (int) height, (int) width, (int) height);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }


    private static void drawTemperatureVignettes(GuiGraphics stack, int width, int height, Player player) {
        ResourceLocation vignetteLocation = null;
        PlayerTemperature tempSystem = player.getData(TFCAmbiental.PLAYER_TEMPERATURE);
        float temperature = tempSystem.getTemperature();

        float BURN_THRESHOLD = TFCAmbientalConfig.burnThreshold.get().floatValue();
        float FREEZE_THRESHOLD = TFCAmbientalConfig.freezeThreshold.get().floatValue();

        float opacity = 0f;
        if (temperature > BURN_THRESHOLD - 2.5f) {
            vignetteLocation = HOT_VIGNETTE;
            opacity = Math.min(0.50f, (temperature - (BURN_THRESHOLD - 2.5f)) / 18);
        } else if (temperature < FREEZE_THRESHOLD + 2.5f) {
            vignetteLocation = COLD_VIGNETTE;
            opacity = Math.min(0.50f, ((FREEZE_THRESHOLD + 2.5f) - temperature) / 18);
        }

        if (vignetteLocation != null) {
            RenderSystem.setShaderTexture(0, vignetteLocation);

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
//            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            RenderSystem.setShader(GameRenderer::getPositionTexShader); // Required shader setup for blit
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
            stack.setColor(1.0F, 1.0F, 1.0F, opacity);
            stack.blit(vignetteLocation, 0, 0, 0, 0, width, height, width, height);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static int getIntFromColor(float red, float green, float blue){
        int R = Math.round(255 * red);
        int G = Math.round(255 * green);
        int B = Math.round(255 * blue);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return 0xFF000000 | R | G | B;
    }
}
