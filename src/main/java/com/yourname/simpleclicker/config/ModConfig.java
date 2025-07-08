package com.yourname.simpleclicker.config;

public class ModConfig {

    // Глобальный переключатель
    public static boolean modEnabled = true;

    // Переключатель для HUD
    public static boolean hudEnabled = true;

    // Настройки ЛКМ
    public static boolean leftClickerEnabled = true;
    public static double leftCps = 12.0;
    public static double leftRandomization = 0.25;

    // Настройки ПКМ
    public static boolean rightClickerEnabled = true;
    public static double rightCps = 12.0;
    public static double rightRandomization = 0.25;

    // Управляющие флаги для потоков
    public static volatile boolean leftClickerActive = false;
    public static volatile boolean rightClickerActive = false;
}