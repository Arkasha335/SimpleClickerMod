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

    /**
     * Это сердце нашего нового мода. Он перехватывает события мыши ДО того,
     * как их обработает игра.
     */
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        // Проверяем, включен ли мод глобально и находимся ли мы в игре
        if (!ModConfig.modEnabled || mc.thePlayer == null || mc.currentScreen != null) {
            return;
        }

        // Обработка Левой Кнопки Мыши (индекс 0)
        if (event.button == 0) {
            if (ModConfig.leftClickerEnabled) {
                // Если кнопка была нажата (состояние true)
                if (event.buttonstate) {
                    // Мы отменяем оригинальное событие, чтобы игра его не видела.
                    event.setCanceled(true);
                    // И активируем наш поток-кликер.
                    ModConfig.leftClickerActive = true;
                } else {
                    // Если кнопка была отпущена, деактивируем поток.
                    ModConfig.leftClickerActive = false;
                }
            }
        }

        // Обработка Правой Кнопки Мыши (индекс 1)
        if (event.button == 1) {
            if (ModConfig.rightClickerEnabled) {
                if (event.buttonstate) {
                    event.setCanceled(true);
                    ModConfig.rightClickerActive = true;
                } else {
                    ModConfig.rightClickerActive = false;
                }
            }
        }
    }

    /**
     * Этот обработчик тиков теперь используется только как "предохранитель".
     * Он отключает кликеры, если игрок открыл инвентарь или другое меню,
     * чтобы избежать случайных кликов в интерфейсе.
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (mc.currentScreen != null) {
                // Если открыт любой GUI, принудительно выключаем оба кликера.
                if (ModConfig.leftClickerActive) {
                    ModConfig.leftClickerActive = false;
                }
                if (ModConfig.rightClickerActive) {
                    ModConfig.rightClickerActive = false;
                }
            }
        }
    }
}