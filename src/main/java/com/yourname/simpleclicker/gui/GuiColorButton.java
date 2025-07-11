package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.*;

public class GuiColorButton extends GuiButton {

    private final boolean active;

    public GuiColorButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean isActive) {
        super(buttonId, x, y, width, height, buttonText);
        this.active = isActive;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            boolean isHovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            // Цвета контура
            Color borderColor = this.active ? new Color(76, 175, 80) : new Color(244, 67, 54);
            if (isHovered) {
                borderColor = borderColor.brighter();
            }

            // Рисуем фон кнопки (темный, полупрозрачный)
            SettingsGui.drawRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 3, 0x5F000000);
            
            // Рисуем контур
            new SettingsGui().drawRoundedRectOutline(this.xPosition, this.yPosition, this.width, this.height, 3, 1.5f, borderColor.getRGB());

            // Текст кнопки
            this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, Color.WHITE.getRGB());
        }
    }
}