package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.Color;

public class GuiColorButton extends GuiButton {

    private final boolean isToggle;

    public GuiColorButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean toggleStatus) {
        super(buttonId, x, y, width, height, buttonText);
        this.isToggle = true;
        this.enabled = toggleStatus; // Используем стандартное поле enabled для хранения состояния
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            boolean isHovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            // Определяем цвета для градиента
            Color topColor = this.enabled ? new Color(20, 140, 50) : new Color(160, 40, 40);
            Color bottomColor = topColor.darker().darker();
            if (isHovered) {
                topColor = topColor.brighter();
                bottomColor = bottomColor.brighter();
            }

            // Рисуем тень для эффекта глубины
            drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, new Color(0, 0, 0, 80).getRGB());
            // Рисуем саму кнопку с градиентом
            drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, topColor.getRGB(), bottomColor.getRGB());
            
            // Рисуем текст и иконку-статус
            String statusIcon = this.enabled ? "§a✔" : "§c✖";
            mc.fontRendererObj.drawStringWithShadow(this.displayString, this.xPosition + 8, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
            mc.fontRendererObj.drawStringWithShadow(statusIcon, this.xPosition + this.width - 18, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
        }
    }
}