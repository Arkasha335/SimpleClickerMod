package com.yourname.simpleclicker.clicker;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import java.util.Random;

public class ClickerThread extends Thread {

    private final int button;
    private final Random random = new Random();
    private final Minecraft mc = Minecraft.getMinecraft();

    public ClickerThread(int button) {
        super("ClickerThread-" + (button == 0 ? "Left" : "Right"));
        this.button = button;
    }

    @Override
    public void run() {
        while (true) {
            try {
                boolean isActive = (button == 0) ? ModConfig.leftClickerActive : ModConfig.rightClickerActive;
                boolean isEnabled = (button == 0) ? ModConfig.leftClickerEnabled : ModConfig.rightClickerEnabled;

                if (!ModConfig.modEnabled || !isActive || !isEnabled) {
                    // Если кликер неактивен, спим дольше, чтобы не тратить ресурсы
                    Thread.sleep(100);
                    continue;
                }

                double cps = (button == 0) ? ModConfig.leftCps : ModConfig.rightCps;
                double randomization = (button == 0) ? ModConfig.leftRandomization : ModConfig.rightRandomization;

                long delay = calculateDelay(cps, randomization);

                // Выполняем клик и спим рассчитанную задержку
                performClick();
                Thread.sleep(delay);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void performClick() {
        KeyBinding key;
        if (this.button == 0) {
            key = mc.gameSettings.keyBindAttack; // Атака (ЛКМ)
        } else {
            key = mc.gameSettings.keyBindUseItem; // Использовать предмет (ПКМ)
        }
        
        // Этот метод напрямую говорит игре "выполни действие этой кнопки один раз"
        // Он не создает новых событий MouseEvent, разрывая порочный круг
        KeyBinding.onTick(key.getKeyCode());
    }

    private long calculateDelay(double cps, double randomization) {
        if (cps <= 0) return 1000;
        long baseDelay = (long) (1000.0 / cps);
        if (randomization > 0) {
            long randomOffset = (long) ((random.nextDouble() - 0.5) * baseDelay * randomization);
            return Math.max(20, baseDelay + randomOffset);
        }
        return Math.max(20, baseDelay);
    }
}