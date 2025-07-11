package com.yourname.simpleclicker.clicker;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

public class ClickerThread extends Thread {

    private final int button; // 0 для ЛКМ, 1 для ПКМ
    private final Random random = new Random();
    private Robot robot;
    private final int mouseButtonMask;
    private final Minecraft mc = Minecraft.getMinecraft();

    public ClickerThread(int button) {
        super("ClickerThread-" + (button == 0 ? "Left" : "Right"));
        this.button = button;

        this.mouseButtonMask = (button == 0) ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK;

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            System.err.println("FATAL: Failed to initialize Robot. Clicks will NOT work.");
            e.printStackTrace();
            this.robot = null;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (robot == null) {
                    Thread.sleep(5000);
                    continue;
                }
                
                // Проверяем, нужно ли кликеру работать
                boolean isActive = (button == 0) ? ModConfig.leftClickerActive : ModConfig.rightClickerActive;
                if (!isActive) {
                    Thread.sleep(50); // Если неактивен, спим подольше
                    continue;
                }
                
                // Получаем актуальные настройки
                double cps = (button == 0) ? ModConfig.leftCps : ModConfig.rightCps;
                double randomization = (button == 0) ? ModConfig.leftRandomization : ModConfig.rightRandomization;
                
                // Выполняем тот самый гибридный клик
                performHybridClick();
                
                // Спим рассчитанную задержку
                Thread.sleep(calculateDelay(cps, randomization));

            } catch (Exception e) {
                // Игнорируем ошибки, чтобы поток не умирал
            }
        }
    }

    private void performHybridClick() {
        // --- ТОТ САМЫЙ МЕХАНИЗМ 1-В-1 ---
        // 1. Нажимаем кнопку на уровне ОС
        robot.mousePress(this.mouseButtonMask);
        try {
            // 2. Делаем микро-задержку для имитации удержания
            Thread.sleep(5 + random.nextInt(10));
        } catch (InterruptedException ignored) {}
        // 3. Отпускаем кнопку на уровне ОС
        robot.mouseRelease(this.mouseButtonMask);
        
        // 4. Форсируем обработку клика внутри игры. Это КЛЮЧЕВОЙ момент.
        KeyBinding key = (this.button == 0) ? mc.gameSettings.keyBindAttack : mc.gameSettings.keyBindUseItem;
        KeyBinding.onTick(key.getKeyCode());
    }

    private long calculateDelay(double cps, double randomization) {
        if (cps <= 0) return 1000;
        long baseDelay = (long) (1000.0 / cps);
        if (randomization > 0) {
            long randomOffset = (long) ((random.nextDouble() - 0.5) * baseDelay * randomization);
            return Math.max(5, baseDelay + randomOffset);
        }
        return Math.max(5, baseDelay);
    }
}