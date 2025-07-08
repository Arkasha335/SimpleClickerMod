package com.yourname.simpleclicker.gui;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class SettingsGui extends GuiScreen {

    // ID кнопок для их идентификации
    private static final int MOD_ENABLED_BUTTON = 0;
    private static final int L_CLICKER_ENABLED_BUTTON = 1;
    private static final int R_CLICKER_ENABLED_BUTTON = 2;
    
    private static final int L_CPS_PLUS = 10;
    private static final int L_CPS_MINUS = 11;
    private static final int L_RAND_PLUS = 12;
    private static final int L_RAND_MINUS = 13;

    private static final int R_CPS_PLUS = 20;
    private static final int R_CPS_MINUS = 21;
    private static final int R_RAND_PLUS = 22;
    private static final int R_RAND_MINUS = 23;


    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        int centerX = this.width / 2;
        int yPos = this.height / 4 - 20;

        // --- Глобальные настройки ---
        this.buttonList.add(new GuiButton(MOD_ENABLED_BUTTON, centerX - 100, yPos, "Mod Enabled: " + (ModConfig.modEnabled ? "ON" : "OFF")));
        yPos += 24;
        
        // --- Настройки ЛКМ ---
        this.buttonList.add(new GuiButton(L_CLICKER_ENABLED_BUTTON, centerX - 155, yPos, 150, 20, "Left Clicker: " + (ModConfig.leftClickerEnabled ? "ON" : "OFF")));
        yPos += 24;
        this.buttonList.add(new GuiButton(L_CPS_MINUS, centerX - 155, yPos, 70, 20, "-"));
        this.buttonList.add(new GuiButton(L_CPS_PLUS, centerX - 85, yPos, 70, 20, "+"));
        yPos += 24;
        this.buttonList.add(new GuiButton(L_RAND_MINUS, centerX - 155, yPos, 70, 20, "-"));
        this.buttonList.add(new GuiButton(L_RAND_PLUS, centerX - 85, yPos, 70, 20, "+"));
        
        yPos = this.height / 4 + 28;

        // --- Настройки ПКМ ---
        this.buttonList.add(new GuiButton(R_CLICKER_ENABLED_BUTTON, centerX + 5, yPos, 150, 20, "Right Clicker: " + (ModConfig.rightClickerEnabled ? "ON" : "OFF")));
        yPos += 24;
        this.buttonList.add(new GuiButton(R_CPS_MINUS, centerX + 5, yPos, 70, 20, "-"));
        this.buttonList.add(new GuiButton(R_CPS_PLUS, centerX + 75, yPos, 70, 20, "+"));
        yPos += 24;
        this.buttonList.add(new GuiButton(R_RAND_MINUS, centerX + 5, yPos, 70, 20, "-"));
        this.buttonList.add(new GuiButton(R_RAND_PLUS, centerX + 75, yPos, 70, 20, "+"));

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case MOD_ENABLED_BUTTON:
                ModConfig.modEnabled = !ModConfig.modEnabled;
                break;
            // ЛКМ
            case L_CLICKER_ENABLED_BUTTON:
                ModConfig.leftClickerEnabled = !ModConfig.leftClickerEnabled;
                break;
            case L_CPS_PLUS:
                ModConfig.leftCps = Math.min(50.0, ModConfig.leftCps + 0.5); // Ограничиваем макс. CPS
                break;
            case L_CPS_MINUS:
                ModConfig.leftCps = Math.max(1.0, ModConfig.leftCps - 0.5); // Ограничиваем мин. CPS
                break;
            case L_RAND_PLUS:
                ModConfig.leftRandomization = Math.min(1.0, ModConfig.leftRandomization + 0.05); // макс. 100%
                break;
            case L_RAND_MINUS:
                ModConfig.leftRandomization = Math.max(0.0, ModConfig.leftRandomization - 0.05); // мин. 0%
                break;
            
            // ПКМ
            case R_CLICKER_ENABLED_BUTTON:
                ModConfig.rightClickerEnabled = !ModConfig.rightClickerEnabled;
                break;
            case R_CPS_PLUS:
                ModConfig.rightCps = Math.min(50.0, ModConfig.rightCps + 0.5);
                break;
            case R_CPS_MINUS:
                ModConfig.rightCps = Math.max(1.0, ModConfig.rightCps - 0.5);
                break;
            case R_RAND_PLUS:
                ModConfig.rightRandomization = Math.min(1.0, ModConfig.rightRandomization + 0.05);
                break;
            case R_RAND_MINUS:
                ModConfig.rightRandomization = Math.max(0.0, ModConfig.rightRandomization - 0.05);
                break;
        }
        // Перерисовываем GUI, чтобы обновить текст на кнопках
        this.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int yPos = this.height / 4 - 35;

        this.drawCenteredString(this.fontRendererObj, "AutoClicker Settings", centerX, yPos, 0xFFFFFF);
        
        // --- ЛКМ ---
        yPos = this.height / 4 + 52;
        this.drawString(this.fontRendererObj, String.format("CPS: %.1f", ModConfig.leftCps), centerX - 155, yPos, 0xFFFFFF);
        yPos += 24;
        this.drawString(this.fontRendererObj, String.format("Rand: %.0f%%", ModConfig.leftRandomization * 100), centerX - 155, yPos, 0xFFFFFF);
        
        // --- ПКМ ---
        yPos = this.height / 4 + 52;
        this.drawString(this.fontRendererObj, String.format("CPS: %.1f", ModConfig.rightCps), centerX + 5, yPos, 0xFFFFFF);
        yPos += 24;
        this.drawString(this.fontRendererObj, String.format("Rand: %.0f%%", ModConfig.rightRandomization * 100), centerX + 5, yPos, 0xFFFFFF);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // GUI не ставит игру на паузу
    }
}