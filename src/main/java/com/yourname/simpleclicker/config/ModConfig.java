package com.yourname.simpleclicker.config;

import com.yourname.simpleclicker.bridge.BridgeMode; // <-- Новый импорт

public class ModConfig {

    // Глобальные переключатели
    public static boolean modEnabled = true;
    public static boolean hudEnabled = true;

    // --- Модуль Auto-Clicker ---
    public static boolean leftClickerEnabled = true;
    public static double leftCps = 12.0;
    public static double leftRandomization = 0.25;

    public static boolean rightClickerEnabled = true;
    public static double rightCps = 12.0;
    public static double rightRandomization = 0.25;

    // --- Модуль Auto-Bridger ---
    public static BridgeMode currentBridgeMode = BridgeMode.DISABLED; // Текущий режим
    public static boolean bridgerEnabled = true; // Главный переключатель модуля

    // --- Управляющие флаги для потоков и ботов ---
    public static volatile boolean leftClickerActive = false;
    public static volatile boolean rightClickerActive = false;
    public static volatile boolean bridgerBotActive = false; // Флаг для активации логики бота
    public static volatile boolean canPlaceBlocks = true; // Флаг для блокировки кликов во время стабилизации
}