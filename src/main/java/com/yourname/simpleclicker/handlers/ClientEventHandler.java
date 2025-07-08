package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    // Флаг для корректной обработки одиночного нажатия клавиши
    private boolean wasToggleKeyPressed = false;

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (SimpleClickerMod.openSettingsKey.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        // Логика перехвата и отмены остается прежней, она работает идеально.
        if (!ModConfig.modEnabled || mc.thePlayer == null || mc.currentScreen != null) {
            return;
        }
        if (event.button == 0 && ModConfig.leftClickerEnabled) {
            if (event.buttonstate) { event.setCanceled(true); ModConfig.leftClickerActive = true; } 
            else { ModConfig.leftClickerActive = false; }
        }
        if (event.button == 1 && ModConfig.rightClickerEnabled) {
            if (event.buttonstate) { event.setCanceled(true); ModConfig.rightClickerActive = true; } 
            else { ModConfig.rightClickerActive = false; }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        // Обработка глобального переключателя
        if (SimpleClickerMod.toggleModKey.isKeyDown()) {
            if (!wasToggleKeyPressed) {
                ModConfig.modEnabled = !ModConfig.modEnabled;
                wasToggleKeyPressed = true;
            }
        } else {
            wasToggleKeyPressed = false;
        }

        // Предохранитель для GUI
        if (mc.currentScreen != null) {
            if (ModConfig.leftClickerActive) ModConfig.leftClickerActive = false;
            if (ModConfig.rightClickerActive) ModConfig.rightClickerActive = false;
        }
    }

    /**
     * Новый обработчик для отрисовки HUD на экране.
     */
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!ModConfig.hudEnabled || mc.gameSettings.showDebugInfo) {
            return;
        }

        String hudText;
        int color;

        if (ModConfig.modEnabled) {
            String activeButtons = "";
            if (ModConfig.leftClickerEnabled) activeButtons += "[LMB] ";
            if (ModConfig.rightClickerEnabled) activeButtons += "[RMB]";
            hudText = "§aClicker: §lON§r " + activeButtons.trim();
            color = Color.GREEN.getRGB(); // Этот цвет сейчас не используется, но может пригодиться
        } else {
            hudText = "§cClicker: §lOFF";
            color = Color.RED.getRGB();
        }

        // Рисуем текст в левом верхнем углу
        mc.fontRendererObj.drawStringWithShadow(hudText, 5, 5, 0xFFFFFF);
    }
}