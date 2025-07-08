package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();
    private Robot robot;

    // Возвращаемся к таймингу на основе миллисекунд
    private long nextLeftClickTime = 0;
    private long nextRightClickTime = 0;

    public ClientEventHandler() {
        // Инициализируем Robot. Оборачиваем в try-catch на случай,
        // если система не поддерживает симуляцию ввода.
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Failed to create Robot for autoclicker!");
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (SimpleClickerMod.openSettingsKey.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Проверяем только в конце тика
        if (event.phase == TickEvent.Phase.END) {
            // Если робот не создался, или мы не в игре - ничего не делаем
            if (robot == null || !ModConfig.modEnabled || mc.thePlayer == null || mc.currentScreen != null) {
                return;
            }

            // Используем новую логику
            handleLeftClicker();
            handleRightClicker();
        }
    }

    private void handleLeftClicker() {
        if (ModConfig.leftClickerEnabled && mc.gameSettings.keyBindAttack.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextLeftClickTime) {
                // Выполняем системный клик
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                // Рассчитываем задержку до следующего клика
                nextLeftClickTime = currentTime + calculateDelayInMillis(ModConfig.leftCps, ModConfig.leftRandomization);
            }
        }
    }

    private void handleRightClicker() {
        if (ModConfig.rightClickerEnabled && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextRightClickTime) {
                // Выполняем системный клик
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK); // BUTTON3 для ПКМ
                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                // Рассчитываем задержку до следующего клика
                nextRightClickTime = currentTime + calculateDelayInMillis(ModConfig.rightCps, ModConfig.rightRandomization);
            }
        }
    }

    // Расчет задержки в миллисекундах
    private long calculateDelayInMillis(double cps, double randomization) {
        if (cps <= 0) return 1000;
        double baseDelay = 1000.0 / cps;
        if (randomization > 0) {
            double randomOffset = (random.nextDouble() - 0.5) * baseDelay * randomization;
            return (long) (baseDelay + randomOffset);
        }
        return (long) baseDelay;
    }
}