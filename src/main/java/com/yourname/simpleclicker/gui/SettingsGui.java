package com.yourname.simpleclicker.gui;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import java.awt.Color;
import java.io.IOException;

public class SettingsGui extends GuiScreen {

    private static final Color COLOR_GREEN = new Color(50, 180, 50);
    private static final Color COLOR_RED = new Color(200, 50, 50);
    private static final Color PANEL_BG = new Color(20, 20, 20, 200);
    private static final Color PANEL_BORDER = new Color(80, 80, 80, 200);

    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        int yPos = this.height / 4 + 20;

        this.buttonList.add(new GuiColorButton(0, centerX - 100, yPos, 200, 20, "Mod Enabled", ModConfig.modEnabled ? COLOR_GREEN : COLOR_RED));
        yPos += 40;

        int columnLeft = centerX - 160;
        int columnRight = centerX + 10;
        int sliderWidth = 150;
        int buttonHeight = 20;
        int yPosLeft = yPos;
        int yPosRight = yPos;

        this.buttonList.add(new GuiColorButton(1, columnLeft, yPosLeft, sliderWidth, buttonHeight, "Left Clicker", ModConfig.leftClickerEnabled ? COLOR_GREEN : COLOR_RED));
        yPosLeft += 24;
        this.buttonList.add(new GuiSlider(3, columnLeft, yPosLeft, sliderWidth, buttonHeight, "CPS", 1.0f, 50.0f, (float) ModConfig.leftCps));
        yPosLeft += 24;
        this.buttonList.add(new GuiSlider(4, columnLeft, yPosLeft, sliderWidth, buttonHeight, "Randomization", 0.0f, 1.0f, (float) ModConfig.leftRandomization));

        this.buttonList.add(new GuiColorButton(2, columnRight, yPosRight, sliderWidth, buttonHeight, "Right Clicker", ModConfig.rightClickerEnabled ? COLOR_GREEN : COLOR_RED));
        yPosRight += 24;
        this.buttonList.add(new GuiSlider(5, columnRight, yPosRight, sliderWidth, buttonHeight, "CPS", 1.0f, 50.0f, (float) ModConfig.rightCps));
        yPosRight += 24;
        this.buttonList.add(new GuiSlider(6, columnRight, yPosRight, sliderWidth, buttonHeight, "Randomization", 0.0f, 1.0f, (float) ModConfig.rightRandomization));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) return;
        if (button instanceof GuiColorButton) {
            switch (button.id) {
                case 0: ModConfig.modEnabled = !ModConfig.modEnabled; break;
                case 1: ModConfig.leftClickerEnabled = !ModConfig.leftClickerEnabled; break;
                case 2: ModConfig.rightClickerEnabled = !ModConfig.rightClickerEnabled; break;
            }
            this.initGui();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiSlider) {
                GuiSlider slider = (GuiSlider) button;
                if (slider.isDragging()) {
                    switch (slider.id) {
                        case 3: ModConfig.leftCps = slider.getValue(); break;
                        case 4: ModConfig.leftRandomization = slider.getValue(); break;
                        case 5: ModConfig.rightCps = slider.getValue(); break;
                        case 6: ModConfig.rightRandomization = slider.getValue(); break;
                    }
                }
            }
        }
        
        this.drawDefaultBackground();
        int panelWidth = 350;
        int panelHeight = 150;
        int startX = (this.width - panelWidth) / 2;
        int startY = (this.height - panelHeight) / 2 - 20;

        drawRect(startX, startY, startX + panelWidth, startY + panelHeight, PANEL_BG.getRGB());
        this.drawHorizontalLine(startX, startX + panelWidth -1, startY, PANEL_BORDER.getRGB());
        this.drawHorizontalLine(startX, startX + panelWidth -1, startY + panelHeight -1, PANEL_BORDER.getRGB());
        this.drawVerticalLine(startX, startY, startY + panelHeight -1, PANEL_BORDER.getRGB());
        this.drawVerticalLine(startX + panelWidth -1, startY, startY + panelHeight -1, PANEL_BORDER.getRGB());
        
        this.drawCenteredString(this.fontRendererObj, "§lSimpleClicker Settings", this.width / 2, this.height / 4, Color.WHITE.getRGB());
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}