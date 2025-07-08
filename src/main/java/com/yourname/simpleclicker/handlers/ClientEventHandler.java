package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
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

    // Снова используем рефлексию для прямого вызова клика
    private static Method clickMouseMethod;
    private static Method rightClickMouseMethod;

    static {
        try {
            clickMouseMethod = Minecraft.class.getDeclaredMethod("func_147116_af");
            rightClickMouseMethod = Minecraft.class.getDeclaredMethod("func_147121_ag");
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
            if (!ModConfig.modEnabled || mc.thePlayer == null || mc.currentScreen != null) {
                return;
            }
            handleLeftClicker();
            handleRightClicker();
        }
    }

    private void handleLeftClicker() {
        KeyBinding key = mc.gameSettings.keyBindAttack;
        if (ModConfig.leftClickerEnabled && key.isKeyDown()) {
            // ШАГ 1: ПОДАВЛЕНИЕ. Говорим игре, что кнопка не зажата, чтобы она не делала стандартное действие.
            KeyBinding.setKeyBindState(key.getKeyCode(), false);

            // ШАГ 2: ПРОВЕРКА ТАЙМЕРА.
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextLeftClickTime) {
                // ШАГ 3: ЗАМЕЩЕНИЕ. Выполняем наш собственный клик напрямую.
                try {
                    if (clickMouseMethod != null) {
                        clickMouseMethod.invoke(mc);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Рассчитываем время следующего клика
                nextLeftClickTime = currentTime + calculateDelayInMillis(ModConfig.leftCps, ModConfig.leftRandomization);
            }
        }
    }

    private void handleRightClicker() {
        KeyBinding key = mc.gameSettings.keyBindUseItem;
        if (ModConfig.rightClickerEnabled && key.isKeyDown()) {
            // Аналогичная логика подавления и замещения для ПКМ
            KeyBinding.setKeyBindState(key.getKeyCode(), false);

            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextRightClickTime) {
                try {
                    if (rightClickMouseMethod != null) {
                        rightClickMouseMethod.invoke(mc);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            return Math.max(1, (long) (baseDelay + randomOffset)); // Убеждаемся, что задержка не 0
        }
        return Math.max(1, (long) baseDelay);
    }
}