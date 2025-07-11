package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.bridge.BridgeController;
import com.yourname.simpleclicker.bridge.BridgeMode;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;
import java.util.Random;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final BridgeController bridgeController = new BridgeController();
    private final Random random = new Random();

    // Переменные для нового, правильного кликера
    private long nextLeftClickTime = 0;
    private long nextRightClickTime = 0;

    // Рефлексия для "нативной" симуляции кликов
    private static Method clickMouseMethod;
    private static Method rightClickMouseMethod;

    static {
        try {
            // Имена методов для 1.8.9
            clickMouseMethod = Minecraft.class.getDeclaredMethod("func_147116_af"); // clickMouse
            rightClickMouseMethod = Minecraft.class.getDeclaredMethod("func_147121_ag"); // rightClickMouse
            clickMouseMethod.setAccessible(true);
            rightClickMouseMethod.setAccessible(true);
        } catch (Exception e) {
            System.err.println("Could not setup reflection for clicker");
            e.printStackTrace();
        }
    }

    // Обработка всех нажатий клавиш
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (mc.currentScreen != null) return;

        if (SimpleClickerMod.keyOpenSettings.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }

        if (SimpleClickerMod.keyToggleMod.isPressed()) {
            ModConfig.modEnabled = !ModConfig.modEnabled;
        }

        if (SimpleClickerMod.keyToggleBridger.isPressed()) {
            if (bridgeController.getCurrentState() == BridgeController.State.IDLE) {
                bridgeController.arm();
            } else {
                bridgeController.disarm();
            }
        }
    }
    
    // Обработка событий мыши (для блокировки камеры и активации бриджера)
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (ModConfig.isCameraLocked && bridgeController.getCurrentState() != BridgeController.State.IDLE) {
            // Блокируем движение мыши, если камера заблокирована
            event.setCanceled(true);
        }

        if (mc.currentScreen == null && mc.thePlayer != null) {
            // Активация бриджера по нажатию ПКМ
            if (bridgeController.getCurrentState() == BridgeController.State.ARMED && event.button == 1) {
                if (event.buttonstate) { // Если кнопка зажата
                    bridgeController.startBridging();
                }
            }
        }
    }

    // Главный игровой цикл (тик)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null) return;
        
        // Логика бриджера
        bridgeController.onTick();

        // Если в GUI, ничего не делаем
        if (mc.currentScreen != null) return;

        // Логика нового автокликера (работает только если бриджер неактивен)
        if (ModConfig.modEnabled && bridgeController.getCurrentState() == BridgeController.State.IDLE) {
            handleAutoClicker();
        }
    }

    private void handleAutoClicker() {
        // Левый кликер
        if (ModConfig.leftClickerEnabled && mc.gameSettings.keyBindAttack.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextLeftClickTime) {
                performLeftClick();
                nextLeftClickTime = currentTime + calculateDelay(ModConfig.leftCps, ModConfig.leftRandomization);
            }
        }
        // Правый кликер
        if (ModConfig.rightClickerEnabled && mc.gameSettings.keyBindUseItem.isKeyDown()) {
             long currentTime = System.currentTimeMillis();
            if (currentTime >= nextRightClickTime) {
                performRightClick();
                nextRightClickTime = currentTime + calculateDelay(ModConfig.rightCps, ModConfig.rightRandomization);
            }
        }
    }

    private long calculateDelay(double cps, double randomization) {
        if (cps <= 0) return 1000;
        double baseDelay = 1000.0 / cps;
        double randomOffset = (random.nextDouble() - 0.5) * baseDelay * randomization;
        return (long) (baseDelay + randomOffset);
    }
    
    private void performLeftClick() {
        try {
            if (clickMouseMethod != null) clickMouseMethod.invoke(mc);
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void performRightClick() {
        try {
            if (rightClickMouseMethod != null) rightClickMouseMethod.invoke(mc);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Отрисовка HUD на экране
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!ModConfig.hudEnabled || mc.gameSettings.showDebugInfo) return;
        
        int yOffset = 5;

        // Статус кликера
        String clickerText = "Clicker: " + (ModConfig.modEnabled ? "§aON" : "§cOFF");
        mc.fontRendererObj.drawStringWithShadow(clickerText, 5, yOffset, 0xFFFFFF);
        yOffset += 10;
        
        // Статус бриджера
        if (ModConfig.bridgerEnabled && ModConfig.currentBridgeMode != BridgeMode.DISABLED) {
            String bridgerStatusText;
            switch(bridgeController.getCurrentState()) {
                case ARMED: bridgerStatusText = "§eARMED"; break;
                case BRIDGING:
                case STABILIZING:
                    bridgerStatusText = "§cBRIDGING"; break;
                default: bridgerStatusText = "§7IDLE"; break;
            }
            mc.fontRendererObj.drawStringWithShadow("Bridger: " + bridgerStatusText, 5, yOffset, 0xFFFFFF);
            
            if(bridgeController.getCurrentState() != BridgeController.State.IDLE) {
                 mc.fontRendererObj.drawStringWithShadow("§7(Mode: " + ModConfig.currentBridgeMode.getDisplayName() + ")", 5, yOffset + 10, 0xFFFFFF);
            }
        }
    }
}