package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.Color;

public class GuiSlider extends GuiButton {

    private float sliderValue;
    private final float valueMin;
    private final float valueMax;
    private final String label;
    private boolean dragging;

    private static final Color TRACK_COLOR = new Color(10, 10, 10, 150);
    private static final Color FILLED_COLOR = new Color(70, 130, 255);

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
    
    public boolean isDragging() {
        return this.dragging;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.dragging) {
            this.sliderValue = (float) (mouseX - this.xPosition) / (float) this.width;
            this.sliderValue = Math.max(0.0F, Math.min(1.0F, this.sliderValue));
            updateDisplayString();
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float) (mouseX - this.xPosition) / (float) this.width;
            this.sliderValue = Math.max(0.0F, Math.min(1.0F, this.sliderValue));
            this.dragging = true;
            updateDisplayString();
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    public float getValue() {
        if (label.contains("CPS")) {
            return (float) (Math.round( (this.valueMin + (this.valueMax - this.valueMin) * this.sliderValue) * 10.0) / 10.0);
        }
        return this.valueMin + (this.valueMax - this.valueMin) * this.sliderValue;
    }

    private void updateDisplayString() {
        if (label.contains("CPS")) {
            this.displayString = String.format("%s: %.1f", label, getValue());
        } else {
            this.displayString = String.format("%s: %.0f%%", label, getValue() * 100);
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, TRACK_COLOR.getRGB());
            int filledWidth = (int) (this.width * this.sliderValue);
            drawRect(this.xPosition, this.yPosition, this.xPosition + filledWidth, this.yPosition + this.height, FILLED_COLOR.getRGB());
            this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}