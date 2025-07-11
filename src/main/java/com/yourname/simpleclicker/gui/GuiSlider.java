package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import java.awt.Color;

public class GuiSlider extends GuiButton {

    private boolean dragging;
    private float sliderValue;
    private final String label;
    private final float valueMin, valueMax;

    public GuiSlider(int id, int x, int y, int width, String label, float min, float max, float current) {
        super(id, x, y, width, 20, ""); // Высота всегда 20
        this.label = label;
        this.valueMin = min;
        this.valueMax = max;
        setValue(current);
    }

    public void setValue(float value) {
        this.sliderValue = (value - this.valueMin) / (this.valueMax - this.valueMin);
        updateDisplayString();
    }

    public float getValue() {
        float val = this.valueMin + (this.valueMax - this.valueMin) * this.sliderValue;
        // Округляем до десятых для CPS, до целых процентов для рандомизации
        return (label.contains("CPS")) ? (float) (Math.round(val * 10.0) / 10.0) : (float) (Math.round(val * 100.0) / 100.0);
    }

    public boolean isDragging() { return this.dragging; }
    public void stopDragging() { this.dragging = false; }

    private void updateDisplayString() {
        float value = getValue();
        if (label.contains("CPS")) {
            this.displayString = String.format("%s: %.1f", label, value);
        } else {
            this.displayString = String.format("%s: %.0f%%", label, value * 100);
        }
    }

    public void updateValue(int mouseX) {
        this.sliderValue = (float) (mouseX - this.xPosition) / (float) this.width;
        this.sliderValue = Math.max(0.0F, Math.min(1.0F, this.sliderValue)); // Ограничиваем от 0 до 1
        updateDisplayString();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.dragging = true;
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            // Рисуем текст значения
            mc.fontRendererObj.drawString(this.displayString, this.xPosition, this.yPosition, 0xFFFFFF);
            
            int sliderY = this.yPosition + 12; // Позиция под текстом
            int sliderHeight = 6;
            
            // Фон слайдера
            SettingsGui.drawRoundedRect(this.xPosition, sliderY, this.xPosition + this.width, sliderY + sliderHeight, 2, 0x7F000000);
            
            // Заполненная часть слайдера
            int filledWidth = (int) (this.width * this.sliderValue);
            SettingsGui.drawRoundedRect(this.xPosition, sliderY, this.xPosition + filledWidth, sliderY + sliderHeight, 2, new Color(76, 175, 80).getRGB());
        }
    }
}