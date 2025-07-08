package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiSlider extends GuiButton {

    private float sliderValue;
    private final float valueMin;
    private final float valueMax;
    private final String label;
    private boolean dragging;

    public GuiSlider(int id, int x, int y, String label, float min, float max, float current) {
        super(id, x, y, 150, 20, "");
        this.label = label;
        this.valueMin = min;
        this.valueMax = max;
        this.sliderValue = (current - min) / (max - min);
        updateDisplayString();
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0; // Отключаем стандартное поведение при наведении
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.dragging) {
            this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
            if (this.sliderValue < 0.0F) this.sliderValue = 0.0F;
            if (this.sliderValue > 1.0F) this.sliderValue = 1.0F;
            updateDisplayString();
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
            if (this.sliderValue < 0.0F) this.sliderValue = 0.0F;
            if (this.sliderValue > 1.0F) this.sliderValue = 1.0F;
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
            mc.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46, this.width / 2, this.height);
            drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46, this.width / 2, this.height);

            // Рисуем ползунок
            int sliderX = this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)) + 4;
            drawRect(sliderX - 2, this.yPosition, sliderX + 2, this.yPosition + this.height, 0xFFFFFFFF);

            this.mouseDragged(mc, mouseX, mouseY);
            this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
        }
    }
}