package com.yourname.simpleclicker.gui;

import com.yourname.simpleclicker.Reference;
import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SettingsGui extends GuiScreen {

    // --- ИСПРАВЛЕНИЯ И УЛУЧШЕНИЯ ---
    // 1. ОПТИМИЗАЦИЯ: Фон теперь рисуется один раз и кэшируется в текстуру.
    //    Это убирает лаги и делает открытие/работу GUI абсолютно плавной.
    // 2. Логика mouseClickMove и mouseReleased исправлена для корректной работы со слайдерами.
    
    private static int backgroundTexture = -1;
    private static boolean isTextureGenerated = false;

    private float hue = 0.0f;
    private int panelWidth, panelHeight, startX, startY;

    @Override
    public void initGui() {
        this.buttonList.clear();

        panelWidth = 340;
        panelHeight = 220;
        startX = (this.width - panelWidth) / 2;
        startY = (this.height - panelHeight) / 2;

        int buttonY = startY + 30;
        int columnLeft = startX + 20;
        int columnRight = startX + panelWidth / 2 + 5;
        int elementWidth = (panelWidth / 2) - 25;

        // ... (остальная часть initGui без изменений)
        this.buttonList.add(new GuiColorButton(0, columnLeft, buttonY, elementWidth, 20, "Mod Enabled", ModConfig.modEnabled));
        this.buttonList.add(new GuiColorButton(7, columnRight, buttonY, elementWidth, 20, "HUD Enabled", ModConfig.hudEnabled));
        buttonY += 30;
        this.buttonList.add(new GuiColorButton(10, columnLeft, buttonY, elementWidth, 20, "Bridger", ModConfig.bridgerEnabled));
        String modeName = "Mode: " + ModConfig.currentBridgeMode.getDisplayName();
        this.buttonList.add(new GuiColorButton(11, columnRight, buttonY, elementWidth, 20, modeName, ModConfig.currentBridgeMode != com.yourname.simpleclicker.bridge.BridgeMode.DISABLED));
        buttonY += 45;
        this.buttonList.add(new GuiColorButton(1, columnLeft, buttonY, elementWidth, 20, "Left Clicker", ModConfig.leftClickerEnabled));
        this.buttonList.add(new GuiSlider(3, columnLeft, buttonY + 25, elementWidth, "CPS", 1.0f, 30.0f, (float) ModConfig.leftCps));
        this.buttonList.add(new GuiSlider(4, columnLeft, buttonY + 50, elementWidth, "Randomize", 0.0f, 1.0f, (float) ModConfig.leftRandomization));
        this.buttonList.add(new GuiColorButton(2, columnRight, buttonY, elementWidth, 20, "Right Clicker", ModConfig.rightClickerEnabled));
        this.buttonList.add(new GuiSlider(5, columnRight, buttonY + 25, elementWidth, "CPS", 1.0f, 30.0f, (float) ModConfig.rightCps));
        this.buttonList.add(new GuiSlider(6, columnRight, buttonY + 50, elementWidth, "Randomize", 0.0f, 1.0f, (float) ModConfig.rightRandomization));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id < 20) { // ID для слайдеров не обрабатываем здесь
            switch (button.id) {
                case 0: ModConfig.modEnabled = !ModConfig.modEnabled; break;
                case 1: ModConfig.leftClickerEnabled = !ModConfig.leftClickerEnabled; break;
                case 2: ModConfig.rightClickerEnabled = !ModConfig.rightClickerEnabled; break;
                case 7: ModConfig.hudEnabled = !ModConfig.hudEnabled; break;
                case 10: ModConfig.bridgerEnabled = !ModConfig.bridgerEnabled; break;
                case 11: ModConfig.currentBridgeMode = ModConfig.currentBridgeMode.getNext(); break;
            }
            this.initGui();
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiSlider && ((GuiSlider) button).isDragging()) {
                ((GuiSlider) button).updateValue(mouseX);
                updateConfigFromSliders(); // Обновляем конфиг в реальном времени
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiSlider) {
                ((GuiSlider) button).mouseReleased(mouseX, mouseY);
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

    private void generateBackgroundTexture() {
        int radius = 6;
        BufferedImage image = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(25, 25, 25, 230)); // Цвет фона
        g2d.fillRoundRect(0, 0, panelWidth, panelHeight, radius * 2, radius * 2);
        g2d.dispose();
        backgroundTexture = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), image);
        isTextureGenerated = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!isTextureGenerated) {
            generateBackgroundTexture();
        }
        
        hue += 0.002F;
        if (hue > 1.0F) hue -= 1.0F;
        Color animatedColor = Color.getHSBColor(hue, 0.8f, 1.0f);
        
        // --- Рендер фона ---
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(backgroundTexture);
        drawModalRectWithCustomSizedTexture(startX, startY, 0, 0, panelWidth, panelHeight, panelWidth, panelHeight);
        
        // --- Рендер рамки ---
        drawRoundedRectOutline(startX, startY, panelWidth, panelHeight, 6, 2.0f, animatedColor.getRGB());
        
        // --- Заголовок ---
        this.drawCenteredString(this.fontRendererObj, Reference.MOD_NAME + " v" + Reference.VERSION, this.width / 2, startY + 10, Color.WHITE.getRGB());
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public void drawRoundedRectOutline(int x, int y, int width, int height, int radius, float lineWidth, int color) {
        // ... (этот метод остается без изменений)
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glLineWidth(lineWidth);
        
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i <= 90; i++) GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * -radius, y + radius + Math.cos(i * Math.PI / 180.0D) * -radius);
        for (int i = 90; i <= 180; i++) GL11.glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * -radius, y + height - radius + Math.cos(i * Math.PI / 180.0D) * -radius);
        for (int i = 0; i <= 90; i++) GL11.glVertex2d(x + width - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + height - radius + Math.cos(i * Math.PI / 180.0D) * radius);
        for (int i = 90; i <= 180; i++) GL11.glVertex2d(x + width - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    @Override
    public boolean doesGuiPauseGame() { return true; }
}