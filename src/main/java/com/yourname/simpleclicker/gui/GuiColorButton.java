package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.Color;

public class GuiColorButton extends GuiButton {

    private final Color color;

    public GuiColorButton(int buttonId, int x, int y, int width, int height, String buttonText, Color color) {
        super(buttonId, x, y, width, height, buttonText);
        this.color = color;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            // Проверяем, наведена ли мышь, чтобы сделать кнопку чуть ярче
            boolean isHovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            Color finalColor = isHovered ? this.color.brighter() : this.color;

            // Рисуем фон кнопки
            drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, finalColor.getRGB());
            // Рисуем текст по центру
            this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
        }
    }
}