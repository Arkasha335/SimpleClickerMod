package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.Color;

public class GuiColorButton extends GuiButton {

    private final boolean isToggle;

    public GuiColorButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean toggleStatus) {
        super(buttonId, x, y, width, height, buttonText);
        this.isToggle = true;
        this.enabled = toggleStatus; // Используем enabled для хранения состояния
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            boolean isHovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            
            // Определяем цвета для градиента
            Color topColor = this.enabled ? new Color(50, 200, 50) : new Color(220, 50, 50);
            Color bottomColor = topColor.darker();
            if (isHovered) {
                topColor = topColor.brighter();
                bottomColor = bottomColor.brighter();
            }

            // Рисуем градиентный фон
            drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, topColor.getRGB(), bottomColor.getRGB());
            
            // Рисуем текст и иконку
            String statusIcon = this.enabled ? "§a✔" : "§c✖";
            mc.fontRendererObj.drawStringWithShadow(this.displayString, this.xPosition + 5, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
            mc.fontRendererObj.drawStringWithShadow(statusIcon, this.xPosition + this.width - 15, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
        }
    }
}