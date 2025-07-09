package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.Color;

public class GuiSlider extends GuiButton {

    public boolean dragging;
    private float sliderValue;
    private final String label;
    private final float valueMin, valueMax;

    public GuiSlider(int id, int x, int y, int width, int height, String label, float min, float max, float current) {
        super(id, x, y, width, height, "");
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
        return (label.contains("CPS")) ? (float) (Math.round(val * 10.0) / 10.0) : val;
    }

    private void updateDisplayString() {
        if (label.contains("CPS")) {
            this.displayString = String.format("%s: §b%.1f", label, getValue());
        } else {
            this.displayString = String.format("%s: §b%.0f%%", label, getValue() * 100);
        }
    }

    // Этот метод теперь будет вызываться из SettingsGui
    public void updateValue(int mouseX) {
        if (this.dragging) {
            this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
            this.sliderValue = Math.max(0.0F, Math.min(1.0F, this.sliderValue));
            updateDisplayString();
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.dragging = true;
            updateValue(mouseX); // Обновляем значение сразу при клике
            return true;
        }
        return false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            
            mc.fontRendererObj.drawString(this.displayString, this.xPosition, this.yPosition, 0xFFFFFF);
            
            int sliderY = this.yPosition + 12;
            int sliderHeight = 4;
            
            drawRect(this.xPosition, sliderY, this.xPosition + this.width, sliderY + sliderHeight, new Color(10, 10, 10, 200).getRGB());
            
            int filledWidth = (int) ((this.width - 8) * this.sliderValue) + 4;
            drawGradientRect(this.xPosition, sliderY, this.xPosition + filledWidth, sliderY + sliderHeight, new Color(80, 130, 255).getRGB(), new Color(120, 170, 255).getRGB());
            
            int handleX = this.xPosition + filledWidth - 4;
            int handleSize = hovered || this.dragging ? 10 : 8;
            drawRect(handleX, sliderY + sliderHeight/2 - handleSize/2, handleX + handleSize, sliderY + sliderHeight/2 + handleSize/2, Color.WHITE.getRGB());
        }
    }
}