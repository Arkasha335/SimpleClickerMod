package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();
    private long nextLeftClickTime = 0;
    private long nextRightClickTime = 0;

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (SimpleClickerMod.openSettingsKey.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Оптимизация: выходим сразу, если мод выключен или мы не в игре
            if (!ModConfig.modEnabled || mc.thePlayer == null || mc.currentScreen != null) {
                return;
            }
            handleLeftClick();
            handleRightClick();
        }
    }

    private void handleLeftClick() {
        if (ModConfig.leftClickerEnabled && mc.gameSettings.keyBindAttack.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextLeftClickTime) {
                performLeftClick();
                nextLeftClickTime = currentTime + calculateDelay(ModConfig.leftCps, ModConfig.leftRandomization);
            }
        }
    }

    private void handleRightClick() {
        if (ModConfig.rightClickerEnabled && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextRightClickTime) {
                performRightClick();
                nextRightClickTime = currentTime + calculateDelay(ModConfig.rightCps, ModConfig.rightRandomization);
            }
        }
    }

    private long calculateDelay(double cps, double randomization) {
        if (cps <= 0) return 1000;
        double baseDelay = 1000.0 / cps;
        if (randomization > 0) {
            double randomOffset = (random.nextDouble() - 0.5) * baseDelay * randomization;
            return (long) (baseDelay + randomOffset);
        }
        return (long) baseDelay;
    }

    // НОВАЯ, ПРАВИЛЬНАЯ РЕАЛИЗАЦИЯ КЛИКОВ
    private void performLeftClick() {
        KeyBinding key = mc.gameSettings.keyBindAttack;
        // Нажимаем клавишу виртуально
        KeyBinding.setKeyBindState(key.getKeyCode(), true);
        // Заставляем игру обработать это нажатие
        KeyBinding.onTick(key.getKeyCode());
        // Сразу же отпускаем, чтобы симулировать одиночный клик
        KeyBinding.setKeyBindState(key.getKeyCode(), false);
    }

    private void performRightClick() {
        KeyBinding key = mc.gameSettings.keyBindUseItem;
        KeyBinding.setKeyBindState(key.getKeyCode(), true);
        KeyBinding.onTick(key.getKeyCode());
        KeyBinding.setKeyBindState(key.getKeyCode(), false);
    }
}