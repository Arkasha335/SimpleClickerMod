package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.Color;

public class GuiColorButton extends GuiButton {

    // --- ИСПРАВЛЕНИЯ И УЛУЧШЕНИЯ ---
    // 1. Отрисовка полностью заменена на простые drawRect для надежности.
    // 2. Добавлены текстовые символы "✔" (галочка) и "✖" (крестик).
    // 3. Цвета контура сделаны более приятными и менее "ядовитыми".

    private final boolean active;

    public GuiColorButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean isActive) {
        super(buttonId, x, y, width, height, buttonText);
        this.active = isActive;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            boolean isHovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            // Определяем цвет контура
            Color borderColor = this.active ? new Color(76, 175, 80) : new Color(211, 47, 47); // Приглушенный зеленый / красный
            if (isHovered) {
                borderColor = borderColor.brighter();
            }

            // Рисуем фон кнопки (темный, полупрозрачный)
            drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0x80000000); // 50% прозрачный черный
            
            // Рисуем контур (обводку)
            drawHorizontalLine(this.xPosition, this.xPosition + this.width -1, this.yPosition, borderColor.getRGB()); // Верх
            drawHorizontalLine(this.xPosition, this.xPosition + this.width -1, this.yPosition + this.height -1, borderColor.getRGB()); // Низ
            drawVerticalLine(this.xPosition, this.yPosition, this.yPosition + this.height -1, borderColor.getRGB()); // Лево
            drawVerticalLine(this.xPosition + this.width -1, this.yPosition, this.yPosition + this.height -1, borderColor.getRGB()); // Право

            // Определяем иконку статуса
            String statusIcon = this.active ? "§a✔" : "§c✖";
            
            // Рисуем текст
            mc.fontRendererObj.drawString(this.displayString, this.xPosition + 5, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
            mc.fontRendererObj.drawString(statusIcon, this.xPosition + this.width - 14, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
        }
    }
}