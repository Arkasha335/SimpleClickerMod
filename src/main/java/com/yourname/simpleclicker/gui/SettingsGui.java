package com.yourname.simpleclicker.gui;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import java.io.IOException;
import java.awt.Color;

public class SettingsGui extends GuiScreen {

    // ID кнопок
    private static final int MOD_ENABLED_BUTTON = 0;
    private static final int L_CLICKER_ENABLED_BUTTON = 1;
    private static final int R_CLICKER_ENABLED_BUTTON = 2;
    private static final int L_CPS_PLUS = 10, L_CPS_MINUS = 11;
    private static final int L_RAND_PLUS = 12, L_RAND_MINUS = 13;
    private static final int R_CPS_PLUS = 20, R_CPS_MINUS = 21;
    private static final int R_RAND_PLUS = 22, R_RAND_MINUS = 23;

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        int centerX = this.width / 2;
        int yPos = this.height / 4;

        // --- Глобальные настройки ---
        this.buttonList.add(new GuiButton(MOD_ENABLED_BUTTON, centerX - 100, yPos, 200, 20, "Mod: " + (ModConfig.modEnabled ? "§aON" : "§cOFF")));
        yPos += 30;

        // --- Колонки для ЛКМ и ПКМ ---
        int columnLeft = centerX - 155;
        int columnRight = centerX + 5;
        int buttonWidthFull = 150;
        int buttonWidthHalf = 73;

        // --- Настройки ЛКМ ---
        int yPosLeft = yPos;
        this.buttonList.add(new GuiButton(L_CLICKER_ENABLED_BUTTON, columnLeft, yPosLeft, buttonWidthFull, 20, "Left Clicker: " + (ModConfig.leftClickerEnabled ? "§aON" : "§cOFF")));
        yPosLeft += 24;
        this.buttonList.add(new GuiButton(L_CPS_MINUS, columnLeft, yPosLeft, buttonWidthHalf, 20, "-"));
        this.buttonList.add(new GuiButton(L_CPS_PLUS, columnLeft + buttonWidthHalf + 4, yPosLeft, buttonWidthHalf, 20, "+"));
        yPosLeft += 24;
        this.buttonList.add(new GuiButton(L_RAND_MINUS, columnLeft, yPosLeft, buttonWidthHalf, 20, "-"));
        this.buttonList.add(new GuiButton(L_RAND_PLUS, columnLeft + buttonWidthHalf + 4, yPosLeft, buttonWidthHalf, 20, "+"));

        // --- Настройки ПКМ ---
        int yPosRight = yPos;
        this.buttonList.add(new GuiButton(R_CLICKER_ENABLED_BUTTON, columnRight, yPosRight, buttonWidthFull, 20, "Right Clicker: " + (ModConfig.rightClickerEnabled ? "§aON" : "§cOFF")));
        yPosRight += 24;
        this.buttonList.add(new GuiButton(R_CPS_MINUS, columnRight, yPosRight, buttonWidthHalf, 20, "-"));
        this.buttonList.add(new GuiButton(R_CPS_PLUS, columnRight + buttonWidthHalf + 4, yPosRight, buttonWidthHalf, 20, "+"));
        yPosRight += 24;
        this.buttonList.add(new GuiButton(R_RAND_MINUS, columnRight, yPosRight, buttonWidthHalf, 20, "-"));
        this.buttonList.add(new GuiButton(R_RAND_PLUS, columnRight + buttonWidthHalf + 4, yPosRight, buttonWidthHalf, 20, "+"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        // Логика кнопок остается прежней, здесь ничего не меняем
        switch (button.id) {
            case MOD_ENABLED_BUTTON: ModConfig.modEnabled = !ModConfig.modEnabled; break;
            case L_CLICKER_ENABLED_BUTTON: ModConfig.leftClickerEnabled = !ModConfig.leftClickerEnabled; break;
            case L_CPS_PLUS: ModConfig.leftCps = Math.min(50.0, ModConfig.leftCps + 0.5); break;
            case L_CPS_MINUS: ModConfig.leftCps = Math.max(1.0, ModConfig.leftCps - 0.5); break;
            case L_RAND_PLUS: ModConfig.leftRandomization = Math.min(1.0, ModConfig.leftRandomization + 0.05); break;
            case L_RAND_MINUS: ModConfig.leftRandomization = Math.max(0.0, ModConfig.leftRandomization - 0.05); break;
            case R_CLICKER_ENABLED_BUTTON: ModConfig.rightClickerEnabled = !ModConfig.rightClickerEnabled; break;
            case R_CPS_PLUS: ModConfig.rightCps = Math.min(50.0, ModConfig.rightCps + 0.5); break;
            case R_CPS_MINUS: ModConfig.rightCps = Math.max(1.0, ModConfig.rightCps - 0.5); break;
            case R_RAND_PLUS: ModConfig.rightRandomization = Math.min(1.0, ModConfig.rightRandomization + 0.05); break;
            case R_RAND_MINUS: ModConfig.rightRandomization = Math.max(0.0, ModConfig.rightRandomization - 0.05); break;
        }
        this.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int yPos = this.height / 4;
        int titleColor = Color.WHITE.getRGB();
        int textColor = Color.LIGHT_GRAY.getRGB();

        this.drawCenteredString(this.fontRendererObj, "AutoClicker Settings", centerX, yPos - 15, titleColor);

        // --- Колонки ---
        int columnLeft = centerX - 155;
        int columnRight = centerX + 5;
        int yPosText = yPos + 54;

        // --- Текст для ЛКМ ---
        String leftCpsText = String.format("CPS: %.1f", ModConfig.leftCps);
        this.drawString(this.fontRendererObj, leftCpsText, columnLeft + 2, yPosText, textColor);
        yPosText += 24;
        String leftRandText = String.format("Rand: %.0f%%", ModConfig.leftRandomization * 100);
        this.drawString(this.fontRendererObj, leftRandText, columnLeft + 2, yPosText, textColor);

        // --- Текст для ПКМ ---
        yPosText = yPos + 54;
        String rightCpsText = String.format("CPS: %.1f", ModConfig.rightCps);
        this.drawString(this.fontRendererObj, rightCpsText, columnRight + 2, yPosText, textColor);
        yPosText += 24;
        String rightRandText = String.format("Rand: %.0f%%", ModConfig.rightRandomization * 100);
        this.drawString(this.fontRendererObj, rightRandText, columnRight + 2, yPosText, textColor);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}