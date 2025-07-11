package com.yourname.simpleclicker.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import java.awt.Color;

public class GuiSlider extends GuiButton {

    // --- ИСПРАВЛЕНИЯ И УЛУЧШЕНИЯ ---
    // 1. Логика dragging полностью переписана, чтобы корректно работать.
    // 2. Отрисовка заменена на простые и надежные drawRect.
    // 3. Значение теперь обновляется только при перетаскивании.

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

    // Устанавливает внутреннее значение слайдера (от 0.0 до 1.0) на основе реального
    public void setValue(float value) {
        this.sliderValue = (value - this.valueMin) / (this.valueMax - this.valueMin);
        updateDisplayString();
    }

    // Возвращает реальное значение (например, CPS от 1 до 30)
    public float getValue() {
        float val = this.valueMin + (this.valueMax - this.valueMin) * this.sliderValue;
        return (label.contains("CPS")) ? (float) (Math.round(val * 10.0) / 10.0) : (float) (Math.round(val * 100.0) / 100.0);
    }

    public boolean isDragging() { return this.dragging; }

    private void updateDisplayString() {
        float value = getValue();
        if (label.contains("CPS")) {
            this.displayString = String.format("%s: %.1f", label, value);
        } else {
            this.displayString = String.format("%s: %.0f%%", label, value * 100);
        }
    }

    // Вызывается при перетаскивании мыши
    public void updateValue(int mouseX) {
        if (this.dragging) {
            // Вычисляем новое значение слайдера (от 0.0 до 1.0)
            this.sliderValue = (float) (mouseX - this.xPosition) / (float) this.width;
            // Ограничиваем, чтобы не выйти за пределы
            this.sliderValue = Math.max(0.0F, Math.min(1.0F, this.sliderValue));
            updateDisplayString();
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        // Проверяем, кликнули ли мы по кнопке
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.dragging = true;
            updateValue(mouseX); // Сразу обновляем значение при клике
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false; // Прекращаем перетаскивание
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            // Рисуем текст (например, "CPS: 12.5")
            mc.fontRendererObj.drawStringWithShadow(this.displayString, this.xPosition, this.yPosition, 0xFFFFFF);

            int sliderY = this.yPosition + 11;
            int sliderHeight = 4;
            
            // Фон (дорожка) слайдера
            drawRect(this.xPosition, sliderY, this.xPosition + this.width, sliderY + sliderHeight, new Color(10, 10, 10, 200).getRGB());
            
            // Заполненная часть слайдера
            int filledWidth = (int) (this.width * this.sliderValue);
            drawRect(this.xPosition, sliderY, this.xPosition + filledWidth, sliderY + sliderHeight, new Color(76, 175, 80).getRGB());
        }
    }
}