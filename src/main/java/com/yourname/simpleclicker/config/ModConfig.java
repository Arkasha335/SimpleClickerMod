package com.yourname.simpleclicker.config;

import com.yourname.simpleclicker.bridge.BridgeMode;

public class ModConfig {

    public static boolean modEnabled = true;
    public static boolean hudEnabled = true;

    // --- Auto-Clicker ---
    public static boolean leftClickerEnabled = true;
    public static double leftCps = 12.0;
    public static double leftRandomization = 0.25;

    public static boolean rightClickerEnabled = true;
    public static double rightCps = 12.0;
    public static double rightRandomization = 0.25;

    // --- Auto-Bridger ---
    public static boolean bridgerEnabled = true;
    public static BridgeMode currentBridgeMode = BridgeMode.DISABLED;

    // --- СИСТЕМНЫЕ ФЛАГИ ---
    public static volatile boolean leftClickerActive = false;
    public static volatile boolean rightClickerActive = false;
    public static volatile boolean bridgerBotActive = false; // Главный флаг для бриджера
    public static volatile boolean isCameraLocked = false;
}