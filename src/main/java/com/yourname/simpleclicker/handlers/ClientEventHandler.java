package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;
import java.util.Random;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();
    private long nextLeftClickTime = 0;
    private long nextRightClickTime = 0;

    // Снова используем рефлексию - это самый быстрый и надежный способ
    private static Method clickMouseMethod;
    private static Method rightClickMouseMethod;

    static {
        try {
            clickMouseMethod = Minecraft.class.getDeclaredMethod("func_147116_af"); // clickMouse
            rightClickMouseMethod = Minecraft.class.getDeclaredMethod("func_147121_ag"); // rightClickMouse
            clickMouseMethod.setAccessible(true);
            rightClickMouseMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
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
        if (event.phase == TickEvent.Phase.END) {
            if (mc.thePlayer != null && mc.currentScreen == null && ModConfig.modEnabled) {
                handleLeftClick();
                handleRightClick();
            }
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
        // Проверяем, включен ли кликер и зажата ли кнопка
        if (ModConfig.rightClickerEnabled && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextRightClickTime) {
                
                // --- УЛУЧШЕННАЯ ЛОГИКА ДЛЯ ПКМ ---
                // Выполняем первый клик
                performRightClick();
                
                // ЭТО ИЗМЕНЕНИЕ: Немедленно вызываем второй клик, чтобы "усилить" нажатие.
                // Это должно помочь с регистрацией установки блоков.
                // Вместо вызова второго клика, который может сбить ритм,
                // мы просто даем игре команду "отправить" клик. Это точнее.
                // В 1.8.9 для этого лучше всего подходит вызов sendClickBlockToController.
                // Однако, чтобы не усложнять рефлексией, простой повторный вызов rightClickMouse
                // является самым безопасным методом для имитации.
                // Мы не будем делать второй вызов, чтобы не сбивать CPS.
                // Вместо этого, мы убедимся, что сама игра "думает", что кнопка зажата.
                // Проблема, скорее всего, в том, что наш клик слишком быстрый.
                
                // ФИНАЛЬНОЕ РЕШЕНИЕ:
                // Метод rightClickMouse() сам по себе идеален, он вызывает sendClickBlockToController.
                // Проблема в том, что Keystrokes и другие моды его не видят.
                // Возвращение к этому методу вернет скорость, но не решит проблему с Keystrokes.
                
                // Мы вернемся к САМОМУ первому варианту, который был быстрым.
                // Проблема строительства, скорее всего, была связана с тиковой системой.
                // Давайте убедимся, что оригинальный, быстрый код работает.
                
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

    private void performLeftClick() {
        try {
            if (clickMouseMethod != null) {
                clickMouseMethod.invoke(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performRightClick() {
        try {
            if (rightClickMouseMethod != null) {
                rightClickMouseMethod.invoke(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}