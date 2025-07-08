package com.yourname.simpleclicker.clicker;

import com.yourname.simpleclicker.config.ModConfig;
import org.lwjgl.input.Mouse;
import java.nio.ByteBuffer;
import java.util.Random;

public class ClickerThread extends Thread {

    private final int button; // 0 для ЛКМ, 1 для ПКМ
    private final Random random = new Random();

    public ClickerThread(int button) {
        super("ClickerThread-" + (button == 0 ? "Left" : "Right"));
        this.button = button;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Спим небольшое время, чтобы не нагружать процессор впустую
                Thread.sleep(50);

                boolean isActive = (button == 0) ? ModConfig.leftClickerActive : ModConfig.rightClickerActive;
                boolean isEnabled = (button == 0) ? ModConfig.leftClickerEnabled : ModConfig.rightClickerEnabled;

                // Если кликер для этой кнопки неактивен (кнопка не зажата) или выключен в меню,
                // просто пропускаем итерацию цикла.
                if (!ModConfig.modEnabled || !isActive || !isEnabled) {
                    continue;
                }

                // Получаем актуальные настройки
                double cps = (button == 0) ? ModConfig.leftCps : ModConfig.rightCps;
                double randomization = (button == 0) ? ModConfig.leftRandomization : ModConfig.rightRandomization;

                long delay = calculateDelay(cps, randomization);

                // Выполняем клик и спим рассчитанную задержку
                performClick();
                Thread.sleep(delay);

            } catch (InterruptedException e) {
                // Поток был прерван, например, при выходе из игры. Это нормально.
                e.printStackTrace();
            }
        }
    }

    private void performClick() {
        // --- Метод "вброса" событий в очередь LWJGL ---
        // Это самый низкоуровневый и надежный способ симуляции клика.
        // Он полностью идентичен нажатию физической кнопки для игры.

        // Создаем буфер для события. Его размер всегда одинаков.
        ByteBuffer buffer = ByteBuffer.allocate(Mouse.getEventSize());

        // Событие "кнопка нажата"
        buffer.put(0, (byte) this.button);      // Индекс кнопки (0=ЛКМ, 1=ПКМ)
        buffer.putInt(4, 1);                    // Состояние (1=нажата)
        buffer.putLong(12, Mouse.getEventNanoseconds()); // Временная метка
        buffer.flip(); // Подготовка буфера к чтению
        Mouse.putEvent(buffer); // Отправка события в очередь
        buffer.clear();

        // Небольшая, но реалистичная задержка между нажатием и отпусканием
        try {
            Thread.sleep(15 + random.nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Событие "кнопка отпущена"
        buffer.put(0, (byte) this.button);
        buffer.putInt(4, 0);                    // Состояние (0=отпущена)
        buffer.putLong(12, Mouse.getEventNanoseconds());
        buffer.flip();
        Mouse.putEvent(buffer);
        buffer.clear();
    }

    private long calculateDelay(double cps, double randomization) {
        if (cps <= 0) return 1000;
        long baseDelay = (long) (1000.0 / cps);
        if (randomization > 0) {
            long randomOffset = (long) ((random.nextDouble() - 0.5) * baseDelay * randomization);
            return Math.max(10, baseDelay + randomOffset); // Минимальная задержка 10 мс
        }
        return Math.max(10, baseDelay);
    }
}