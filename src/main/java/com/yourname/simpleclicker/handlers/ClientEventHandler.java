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

    // Задержки теперь считаются в тиках, а не миллисекундах
    private int leftClickerDelay = 0;
    private int rightClickerDelay = 0;

    // Флаги, чтобы знать, когда нужно "отпустить" кнопку
    private boolean leftClicking = false;
    private boolean rightClicking = false;

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (SimpleClickerMod.openSettingsKey.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Мы работаем только в начале тика, чтобы другие моды успели среагировать
        if (event.phase == TickEvent.Phase.START) {
            if (!ModConfig.modEnabled || mc.thePlayer == null || mc.currentScreen != null) {
                // Если мод выключается, сбрасываем состояние кнопок, чтобы они не остались "зажатыми"
                if (leftClicking) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                    leftClicking = false;
                }
                if (rightClicking) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    rightClicking = false;
                }
                return;
            }

            // --- НОВАЯ ЛОГИКА ---
            handleLeftClicker();
            handleRightClicker();
        }
    }

    private void handleLeftClicker() {
        KeyBinding key = mc.gameSettings.keyBindAttack;

        // Если мы кликали в прошлом тике, отпускаем кнопку сейчас
        if (leftClicking) {
            KeyBinding.setKeyBindState(key.getKeyCode(), false);
            leftClicking = false;
        }

        // Если задержка еще не прошла, уменьшаем ее и выходим
        if (leftClickerDelay > 0) {
            leftClickerDelay--;
            return;
        }
        
        // Проверяем, включен ли кликер и зажата ли кнопка игроком
        if (ModConfig.leftClickerEnabled && key.isKeyDown()) {
            // "Нажимаем" кнопку виртуально
            KeyBinding.setKeyBindState(key.getKeyCode(), true);
            // Устанавливаем флаг, чтобы отпустить ее на следующем тике
            leftClicking = true;
            // Рассчитываем задержку до следующего клика в тиках
            leftClickerDelay = calculateDelayInTicks(ModConfig.leftCps, ModConfig.leftRandomization);
        }
    }

    private void handleRightClicker() {
        KeyBinding key = mc.gameSettings.keyBindUseItem;

        if (rightClicking) {
            KeyBinding.setKeyBindState(key.getKeyCode(), false);
            rightClicking = false;
        }

        if (rightClickerDelay > 0) {
            rightClickerDelay--;
            return;
        }

        if (ModConfig.rightClickerEnabled && key.isKeyDown()) {
            KeyBinding.setKeyBindState(key.getKeyCode(), true);
            rightClicking = true;
            rightClickerDelay = calculateDelayInTicks(ModConfig.rightCps, ModConfig.rightRandomization);
        }
    }

    // Расчет задержки в тиках (1 секунда = 20 тиков)
    private int calculateDelayInTicks(double cps, double randomization) {
        if (cps <= 0) return 20;
        double ticksPerSecond = 20.0;
        double baseDelay = ticksPerSecond / cps;
        if (randomization > 0) {
            double randomOffset = (random.nextDouble() - 0.5) * baseDelay * randomization;
            // Убеждаемся, что задержка не меньше 1 тика
            return Math.max(1, (int) Math.round(baseDelay + randomOffset));
        }
        return Math.max(1, (int) Math.round(baseDelay));
    }
}