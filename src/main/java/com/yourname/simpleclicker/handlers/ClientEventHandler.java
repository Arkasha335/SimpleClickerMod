package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (SimpleClickerMod.openSettingsKey.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (!ModConfig.modEnabled || mc.thePlayer == null || mc.currentScreen != null) {
            return;
        }

        if (event.button == 0) { // ЛКМ
            if (ModConfig.leftClickerEnabled) {
                if (event.buttonstate) { // Нажата
                    event.setCanceled(true);
                    ModConfig.leftClickerActive = true;
                } else { // Отпущена
                    ModConfig.leftClickerActive = false;
                }
            }
        }

        if (event.button == 1) { // ПКМ
            if (ModConfig.rightClickerEnabled) {
                if (event.buttonstate) { // Нажата
                    event.setCanceled(true);
                    ModConfig.rightClickerActive = true;
                } else { // Отпущена
                    ModConfig.rightClickerActive = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (mc.currentScreen != null) {
                if (ModConfig.leftClickerActive) ModConfig.leftClickerActive = false;
                if (ModConfig.rightClickerActive) ModConfig.rightClickerActive = false;
            }
        }
    }
}