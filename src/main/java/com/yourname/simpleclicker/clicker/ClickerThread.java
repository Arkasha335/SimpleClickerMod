package com.yourname.simpleclicker.clicker;

import com.yourname.simpleclicker.config.ModConfig;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Random;

public class ClickerThread extends Thread {

    private final int button;
    private final Random random = new Random();
    private Robot robot;
    private final int mouseButtonMask;

    public ClickerThread(int button) {
        super("ClickerThread-" + (button == 0 ? "Left" : "Right"));
        this.button = button;

        if (button == 0) {
            this.mouseButtonMask = InputEvent.BUTTON1_DOWN_MASK;
        } else {
            this.mouseButtonMask = InputEvent.BUTTON3_DOWN_MASK;
        }

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Failed to initialize Robot for clicker thread. Clicker will not work.");
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
                
                Thread.sleep(10);

                boolean isActive = (button == 0) ? ModConfig.leftClickerActive : ModConfig.rightClickerActive;
                boolean isEnabled = (button == 0) ? ModConfig.leftClickerEnabled : ModConfig.rightClickerEnabled;

                if (!ModConfig.modEnabled || !isActive || !isEnabled) {
                    continue;
                }

                // Новая проверка для Bridger
                if (!ModConfig.canPlaceBlocks) {
                    continue;
                }

                double cps = (button == 0) ? ModConfig.leftCps : ModConfig.rightCps;
                double randomization = (button == 0) ? ModConfig.leftRandomization : ModConfig.rightRandomization;

                long delay = calculateDelay(cps, randomization);

                performClick();
                Thread.sleep(delay);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void performClick() {
        robot.mousePress(this.mouseButtonMask);
        try {
            Thread.sleep(10 + random.nextInt(15));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.mouseRelease(this.mouseButtonMask);
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