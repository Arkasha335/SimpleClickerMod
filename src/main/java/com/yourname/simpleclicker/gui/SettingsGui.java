package com.yourname.simpleclicker.gui;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;
import java.awt.Color;
import java.io.IOException;

public class SettingsGui extends GuiScreen {

    private float hue = 0.0f;
    private int panelWidth, panelHeight, startX, startY;

    @Override
    public void initGui() {
        this.buttonList.clear();
        
        panelWidth = 340;
        panelHeight = 210;
        startX = (this.width - panelWidth) / 2;
        startY = (this.height - panelHeight) / 2;

        int buttonY = startY + 45;
        this.buttonList.add(new GuiColorButton(0, startX + 20, buttonY, panelWidth - 40, 20, "§lMod Enabled", ModConfig.modEnabled));
        
        buttonY += 24;
        this.buttonList.add(new GuiColorButton(7, startX + 20, buttonY, (panelWidth / 2) - 25, 20, "§lHUD", ModConfig.hudEnabled));
        this.buttonList.add(new GuiButton(8, startX + (panelWidth / 2) + 5, buttonY, (panelWidth / 2) - 25, 20, "Rebind Keys"));
        
        buttonY += 45;
        int columnLeft = startX + 20;
        int columnRight = startX + panelWidth / 2 + 5;
        int sliderWidth = (panelWidth / 2) - 25;
        
        this.buttonList.add(new GuiColorButton(1, columnLeft, buttonY, sliderWidth, 20, "§lLeft Clicker", ModConfig.leftClickerEnabled));
        this.buttonList.add(new GuiSlider(3, columnLeft, buttonY + 35, sliderWidth, 20, "CPS", 1.0f, 50.0f, (float) ModConfig.leftCps));
        this.buttonList.add(new GuiSlider(4, columnLeft, buttonY + 65, sliderWidth, 20, "Randomize", 0.0f, 1.0f, (float) ModConfig.leftRandomization));

        this.buttonList.add(new GuiColorButton(2, columnRight, buttonY, sliderWidth, 20, "§lRight Clicker", ModConfig.rightClickerEnabled));
        this.buttonList.add(new GuiSlider(5, columnRight, buttonY + 35, sliderWidth, 20, "CPS", 1.0f, 50.0f, (float) ModConfig.rightCps));
        this.buttonList.add(new GuiSlider(6, columnRight, buttonY + 65, sliderWidth, 20, "Randomize", 0.0f, 1.0f, (float) ModConfig.rightRandomization));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        // --- ИСПРАВЛЕННАЯ ЛОГИКА ---
        switch (button.id) {
            case 0: ModConfig.modEnabled = !ModConfig.modEnabled; break;
            case 1: ModConfig.leftClickerEnabled = !ModConfig.leftClickerEnabled; break;
            case 2: ModConfig.rightClickerEnabled = !ModConfig.rightClickerEnabled; break;
            case 7: ModConfig.hudEnabled = !ModConfig.hudEnabled; break;
            case 8:
                mc.displayGuiScreen(new GuiControls(this, mc.gameSettings));
                return; // Выходим, чтобы не пересоздавать GUI
        }
        // После изменения конфига, просто пересоздаем весь GUI.
        // Он автоматически считает новые значения и создаст кнопки с правильным видом.
        this.initGui();
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiSlider) {
                ((GuiSlider) button).updateValue(mouseX);
            }
        }
        updateConfigFromSliders();
    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiSlider) {
                ((GuiSlider) button).dragging = false;
            }
        }
    }
    private void updateConfigFromSliders() {
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiSlider) {
                GuiSlider slider = (GuiSlider) button;
                switch (slider.id) {
                    case 3: ModConfig.leftCps = slider.getValue(); break;
                    case 4: ModConfig.leftRandomization = slider.getValue(); break;
                    case 5: ModConfig.rightCps = slider.getValue(); break;
                    case 6: ModConfig.rightRandomization = slider.getValue(); break;
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        hue += 0.002F;
        if (hue > 1.0F) hue -= 1.0F;
        Color animatedColor = Color.getHSBColor(hue, 0.8f, 1.0f);

        this.drawDefaultBackground();
        drawRoundedRect(startX - 1, startY - 1, startX + panelWidth + 1, startY + panelHeight + 1, 7, animatedColor.getRGB());
        drawRoundedRect(startX, startY, startX + panelWidth, startY + panelHeight, 6, new Color(25, 25, 25, 230).getRGB());
        
        this.drawCenteredString(this.fontRendererObj, "§lSimpleClicker v4.1", this.width / 2, startY + 15, Color.WHITE.getRGB());
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public static void drawRoundedRect(int x, int y, int x2, int y2, int rad, int color) {
        // Код этого метода не менялся, но он все еще нужен
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        x *= 2; y *= 2; x2 *= 2; y2 *= 2; rad *= 2;
        net.minecraft.client.renderer.GlStateManager.enableBlend(); net.minecraft.client.renderer.GlStateManager.disableTexture2D();
        net.minecraft.client.renderer.GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >> 24 & 255) / 255.0F);
        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i <= 90; i += 3) GL11.glVertex2d(x + rad + Math.sin(Math.toRadians(i)) * rad * -1.0D, y + rad + Math.cos(Math.toRadians(i)) * rad * -1.0D);
        for (int i = 90; i <= 180; i += 3) GL11.glVertex2d(x + rad + Math.sin(Math.toRadians(i)) * rad * -1.0D, y2 - rad + Math.cos(Math.toRadians(i)) * rad * -1.0D);
        for (int i = 0; i <= 90; i += 3) GL11.glVertex2d(x2 - rad + Math.sin(Math.toRadians(i)) * rad, y2 - rad + Math.cos(Math.toRadians(i)) * rad);
        for (int i = 90; i <= 180; i += 3) GL11.glVertex2d(x2 - rad + Math.sin(Math.toRadians(i)) * rad, y + rad + Math.cos(Math.toRadians(i)) * rad);
        GL11.glEnd();
        net.minecraft.client.renderer.GlStateManager.enableTexture2D(); net.minecraft.client.renderer.GlStateManager.disableBlend();
        GL11.glScaled(2.0D, 2.0D, 2.0D);
        GL11.glPopAttrib();
    }
    
    @Override
    public boolean doesGuiPauseGame() { return true; }
}