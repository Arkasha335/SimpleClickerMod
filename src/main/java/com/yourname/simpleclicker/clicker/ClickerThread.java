package com.yourname.simpleclicker.clicker;

import com.yourname.simpleclicker.config.ModConfig;
import org.lwjgl.input.Mouse;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Random;

public class ClickerThread extends Thread {

    private final int button;
    private final Random random = new Random();

    // --- НОВЫЙ БЛОК С РЕФЛЕКСИЕЙ ---
    // Здесь мы будем хранить "отмычки" к спрятанным методам
    private static Method methodGetEventSize;
    private static Method methodPutEvent;

    // Этот статический блок выполнится один раз при загрузке класса.
    // Он найдет спрятанные методы и сделает их доступными для нас.
    static {
        try {
            methodGetEventSize = Mouse.class.getDeclaredMethod("getEventSize");
            methodPutEvent = Mouse.class.getDeclaredMethod("putEvent", ByteBuffer.class);
            // Делаем методы доступными, даже если они приватные
            methodGetEventSize.setAccessible(true);
            methodPutEvent.setAccessible(true);
        } catch (NoSuchMethodException e) {
            // Если методы не найдены (например, другая версия LWJGL), выводим ошибку.
            throw new RuntimeException("Failed to access LWJGL methods via reflection", e);
        }
    }
    // --- КОНЕЦ НОВОГО БЛОКА ---


    public ClickerThread(int button) {
        super("ClickerThread-" + (button == 0 ? "Left" : "Right"));
        this.button = button;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);

                boolean isActive = (button == 0) ? ModConfig.leftClickerActive : ModConfig.rightClickerActive;
                boolean isEnabled = (button == 0) ? ModConfig.leftClickerEnabled : ModConfig.rightClickerEnabled;

                if (!ModConfig.modEnabled || !isActive || !isEnabled) {
                    continue;
                }

                double cps = (button == 0) ? ModConfig.leftCps : ModConfig.rightCps;
                double randomization = (button == 0) ? ModConfig.leftRandomization : ModConfig.rightRandomization;

                long delay = calculateDelay(cps, randomization);

                performClick();
                Thread.sleep(delay);

            } catch (Exception e) {
                // Ловим общие исключения, так как рефлексия может их выбрасывать
                e.printStackTrace();
            }
        }
    }

    private void performClick() throws Exception { // Добавляем throws Exception
        // --- ИСПРАВЛЕННЫЙ МЕТОД КЛИКА ---

        // Вызываем getEventSize() с помощью рефлексии
        int eventSize = (int) methodGetEventSize.invoke(null);
        ByteBuffer buffer = ByteBuffer.allocate(eventSize);

        // Событие "кнопка нажата"
        buffer.put(0, (byte) this.button);
        buffer.putInt(4, 1);
        buffer.putLong(12, Mouse.getEventNanoseconds());
        buffer.flip();
        // Вызываем putEvent(buffer) с помощью рефлексии
        methodPutEvent.invoke(null, buffer);
        buffer.clear();

        // Задержка между нажатием и отпусканием
        Thread.sleep(15 + random.nextInt(10));

        // Событие "кнопка отпущена"
        buffer.put(0, (byte) this.button);
        buffer.putInt(4, 0);
        buffer.putLong(12, Mouse.getEventNanoseconds());
        buffer.flip();
        // Снова вызываем putEvent(buffer) с помощью рефлексии
        methodPutEvent.invoke(null, buffer);
        buffer.clear();
    }

    private long calculateDelay(double cps, double randomization) {
        if (cps <= 0) return 1000;
        long baseDelay = (long) (1000.0 / cps);
        if (randomization > 0) {
            long randomOffset = (long) ((random.nextDouble() - 0.5) * baseDelay * randomization);
            return Math.max(10, baseDelay + randomOffset);
        }
        return Math.max(10, baseDelay);
    }
}