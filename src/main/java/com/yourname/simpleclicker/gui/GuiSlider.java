package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.Color;

public class GuiSlider extends GuiButton {

    private boolean dragging;
    private float sliderValue;
    private final String label;
    private final float valueMin, valueMax;

    public GuiSlider(int id, int x, int y, int width, String label, float min, float max, float current) {
        super(id, x, y, width, 20, "");
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
        // Округляем значения для красивого отображения
        if (label.contains("CPS")) {
            return (float) (Math.round(val * 10.0) / 10.0);
        }
        return (float) (Math.round(val * 100.0) / 100.0);
    }

    private void updateDisplayString() {
        float value = getValue();
        if (label.contains("CPS")) {
            this.displayString = String.format("%s: %.1f", label, value);
        } else {
            this.displayString = String.format("%s: %.0f%%", label, value * 100);
        }
    }

    // --- ИЗМЕНЕНИЕ: этот метод теперь вызывается из SettingsGui ---
    public void onDrag(int mouseX) {
        if (this.dragging) {
            this.sliderValue = (float) (mouseX - (this.xPosition)) / (float) (this.width);
            this.sliderValue = Math.max(0.0F, Math.min(1.0F, this.sliderValue)); // Ограничение от 0.0 до 1.0
            updateDisplayString();
        }
    }

    // --- ИЗМЕНЕНИЕ: mousePressed теперь просто устанавливает флаг ---
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            mc.fontRendererObj.drawStringWithShadow(this.displayString, this.xPosition, this.yPosition, 0xFFFFFF);
            int sliderY = this.yPosition + 11;
            int sliderHeight = 4;
            drawRect(this.xPosition, sliderY, this.xPosition + this.width, sliderY + sliderHeight, new Color(10, 10, 10, 200).getRGB());
            int filledWidth = (int) (this.width * this.sliderValue);
            drawRect(this.xPosition, sliderY, this.xPosition + filledWidth, sliderY + sliderHeight, new Color(76, 175, 80).getRGB());
        }
    }
}