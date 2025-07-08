package com.yourname.simpleclicker.gui;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import java.awt.Color;
import java.io.IOException;

public class SettingsGui extends GuiScreen {

    private static final int MOD_ENABLED_BUTTON = 0;
    private static final int L_CLICKER_ENABLED_BUTTON = 1;
    private static final int R_CLICKER_ENABLED_BUTTON = 2;

    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        int yPos = this.height / 4;

        // --- Глобальные настройки ---
        this.buttonList.add(new GuiButton(MOD_ENABLED_BUTTON, centerX - 100, yPos, 200, 20, "Mod Enabled: " + getStatus(ModConfig.modEnabled)));
        yPos += 30;

        // --- Колонки ---
        int columnLeft = centerX - 160;
        int columnRight = centerX + 10;
        int yPosLeft = yPos;
        int yPosRight = yPos;

        // --- Левая колонка (ЛКМ) ---
        this.drawCenteredString(this.fontRendererObj, "Left Mouse Button", columnLeft + 75, yPosLeft, Color.WHITE.getRGB());
        yPosLeft += 15;
        this.buttonList.add(new GuiButton(L_CLICKER_ENABLED_BUTTON, columnLeft, yPosLeft, 150, 20, "Enabled: " + getStatus(ModConfig.leftClickerEnabled)));
        yPosLeft += 24;
        this.buttonList.add(new GuiSlider(3, columnLeft, yPosLeft, "CPS", 1.0f, 50.0f, (float) ModConfig.leftCps));
        yPosLeft += 24;
        this.buttonList.add(new GuiSlider(4, columnLeft, yPosLeft, "Randomization", 0.0f, 1.0f, (float) ModConfig.leftRandomization));

        // --- Правая колонка (ПКМ) ---
        this.drawCenteredString(this.fontRendererObj, "Right Mouse Button", columnRight + 75, yPosRight, Color.WHITE.getRGB());
        yPosRight += 15;
        this.buttonList.add(new GuiButton(R_CLICKER_ENABLED_BUTTON, columnRight, yPosRight, 150, 20, "Enabled: " + getStatus(ModConfig.rightClickerEnabled)));
        yPosRight += 24;
        this.buttonList.add(new GuiSlider(5, columnRight, yPosRight, "CPS", 1.0f, 50.0f, (float) ModConfig.rightCps));
        yPosRight += 24;
        this.buttonList.add(new GuiSlider(6, columnRight, yPosRight, "Randomization", 0.0f, 1.0f, (float) ModConfig.rightRandomization));
    }

    private String getStatus(boolean status) {
        return status ? "§aON" : "§cOFF";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == MOD_ENABLED_BUTTON) ModConfig.modEnabled = !ModConfig.modEnabled;
        if (button.id == L_CLICKER_ENABLED_BUTTON) ModConfig.leftClickerEnabled = !ModConfig.leftClickerEnabled;
        if (button.id == R_CLICKER_ENABLED_BUTTON) ModConfig.rightClickerEnabled = !ModConfig.rightClickerEnabled;
        this.initGui(); // Перерисовываем GUI, чтобы обновить текст на кнопках
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiSlider) {
                GuiSlider slider = (GuiSlider) button;
                if (slider.id == 3) ModConfig.leftCps = slider.getValue();
                if (slider.id == 4) ModConfig.leftRandomization = slider.getValue();
                if (slider.id == 5) ModConfig.rightCps = slider.getValue();
                if (slider.id == 6) ModConfig.rightRandomization = slider.getValue();
            }
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiSlider) {
                // Это гарантирует, что значение обновится, даже если был просто клик, а не перетаскивание
                mouseClickMove(mouseX, mouseY, state, 0);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return true; // Теперь меню ставит игру на паузу, что более удобно для настройки
    }
}