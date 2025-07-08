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

    private Minecraft mc = Minecraft.getMinecraft();
    private Random random = new Random();
    private long nextLeftClickTime = 0;
    private long nextRightClickTime = 0;

    // Для доступа к приватным методам Minecraft, мы храним их объекты
    private static Method clickMouseMethod;
    private static Method rightClickMouseMethod;

    static {
        try {
            // Инициализируем методы через рефлексию.
            // Имена "func_147116_af" (clickMouse) и "func_147121_ag" (rightClickMouse) актуальны для 1.8.9
            clickMouseMethod = Minecraft.class.getDeclaredMethod("func_147116_af");
            rightClickMouseMethod = Minecraft.class.getDeclaredMethod("func_147121_ag");
            clickMouseMethod.setAccessible(true);
            rightClickMouseMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            // Если методы не найдены (например, другая версия игры), выводим ошибку
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        // Проверяем, была ли нажата наша клавиша для открытия меню
        if (SimpleClickerMod.openSettingsKey.isPressed()) {
            // Открываем GUI
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Выполняем логику только в конце тика, чтобы избежать конфликтов
        if (event.phase == TickEvent.Phase.END) {
            // Проверяем, что игрок в мире, не в меню и мод включен
            if (mc.thePlayer != null && mc.currentScreen == null && ModConfig.modEnabled) {
                handleLeftClick();
                handleRightClick();
            }
        }
    }

    private void handleLeftClick() {
        // Проверяем, включен ли кликер для ЛКМ и зажата ли кнопка
        if (ModConfig.leftClickerEnabled && mc.gameSettings.keyBindAttack.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextLeftClickTime) {
                performLeftClick();
                // Рассчитываем время следующего клика
                nextLeftClickTime = currentTime + calculateDelay(ModConfig.leftCps, ModConfig.leftRandomization);
            }
        }
    }

    private void handleRightClick() {
        // Аналогичная логика для ПКМ
        if (ModConfig.rightClickerEnabled && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextRightClickTime) {
                performRightClick();
                nextRightClickTime = currentTime + calculateDelay(ModConfig.rightCps, ModConfig.rightRandomization);
            }
        }
    }
    
    private long calculateDelay(double cps, double randomization) {
        if (cps <= 0) return 1000; // Безопасная проверка
        double baseDelay = 1000.0 / cps;
        // (random.nextDouble() - 0.5) дает случайное число от -0.5 до 0.5
        // Умножаем это на базовую задержку и на коэффициент рандомизации
        double randomOffset = (random.nextDouble() - 0.5) * baseDelay * randomization;
        return (long) (baseDelay + randomOffset);
    }
    
    private void performLeftClick() {
        try {
            // Вызываем приватный метод clickMouse() через рефлексию
            if (clickMouseMethod != null) {
                clickMouseMethod.invoke(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void performRightClick() {
        try {
            // Вызываем приватный метод rightClickMouse() через рефлексию
            if (rightClickMouseMethod != null) {
                rightClickMouseMethod.invoke(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}