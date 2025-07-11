package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.bridge.BridgeController;
import com.yourname.simpleclicker.bridge.BridgeMode;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final BridgeController bridgeController = new BridgeController();
    private final Random random = new Random();

    private boolean wasToggleModKeyPressed = false;
    private boolean wasToggleBridgerKeyPressed = false;

    private long nextLeftClickTime = 0;
    private long nextRightClickTime = 0;
    
    // --- УДАЛЕНО: Рефлексия больше не нужна, используем KeyBinding.onTick() ---

    // ... (onKeyInput и onMouseEvent остаются без изменений) ...
    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (SimpleClickerMod.keyOpenSettings.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (ModConfig.isCameraLocked) {
            event.setCanceled(true);
        }

        if (mc.currentScreen == null && mc.thePlayer != null) {
            if (event.button == 1 && ModConfig.bridgerEnabled) {
                if (bridgeController.getCurrentState() == BridgeController.State.ARMED && event.buttonstate) {
                    bridgeController.startBridging();
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null) return;
        handleKeyToggles();
        bridgeController.onTick();
        if (mc.currentScreen != null) {
            if (bridgeController.getCurrentState() != BridgeController.State.IDLE) {
                bridgeController.disarm();
            }
            return;
        }
        if (ModConfig.modEnabled && bridgeController.getCurrentState() == BridgeController.State.IDLE) {
            handleAutoClicker();
        }
    }

    // --- НОВАЯ, ИДЕАЛЬНАЯ ЛОГИКА АВТОКЛИКЕРА ---
    private void handleAutoClicker() {
        if (ModConfig.leftClickerEnabled && mc.gameSettings.keyBindAttack.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextLeftClickTime) {
                // Имитируем короткое нажатие + вызываем onTick
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
                // Рассчитываем следующую задержку по вашей формуле
                nextLeftClickTime = currentTime + calculateDelay(ModConfig.leftCps, ModConfig.leftRandomization);
            }
        }
        if (ModConfig.rightClickerEnabled && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextRightClickTime) {
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                nextRightClickTime = currentTime + calculateDelay(ModConfig.rightCps, ModConfig.rightRandomization);
            }
        }
    }

    // --- ТА САМАЯ ФОРМУЛА РАСЧЕТА ЗАДЕРЖКИ ---
    private long calculateDelay(double cps, double randomization) {
        if (cps <= 0) return 1000;
        long baseDelay = (long) (1000.0 / cps);
        if (randomization > 0) {
            long randomOffset = (long) ((random.nextDouble() - 0.5) * baseDelay * randomization);
            // Убеждаемся, что задержка не слишком маленькая
            return Math.max(5, baseDelay + randomOffset); // Поставил 5 мс, т.к. CPS до 100
        }
        return Math.max(5, baseDelay);
    }
    
    // ... (handleKeyToggles и onRenderOverlay остаются без изменений) ...
    private void handleKeyToggles() {
        if (SimpleClickerMod.keyToggleMod.isKeyDown()) {
            if (!wasToggleModKeyPressed) {
                ModConfig.modEnabled = !ModConfig.modEnabled;
                wasToggleModKeyPressed = true;
            }
        } else {
            wasToggleModKeyPressed = false;
        }

        if (SimpleClickerMod.keyToggleBridger.isKeyDown()) {
            if (!wasToggleBridgerKeyPressed) {
                if (bridgeController.getCurrentState() == BridgeController.State.IDLE) {
                    bridgeController.arm();
                } else {
                    bridgeController.disarm();
                }
                wasToggleBridgerKeyPressed = true;
            }
        } else {
            wasToggleBridgerKeyPressed = false;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!ModConfig.hudEnabled || mc.gameSettings.showDebugInfo) return;
        
        int yOffset = 5;

        mc.fontRendererObj.drawStringWithShadow("Clicker: " + (ModConfig.modEnabled ? "§aON" : "§cOFF"), 5, yOffset, 0xFFFFFF);
        yOffset += 10;
        
        if (ModConfig.bridgerEnabled && ModConfig.currentBridgeMode != BridgeMode.DISABLED) {
            String bridgerStatusText;
            switch(bridgeController.getCurrentState()) {
                case ARMED: bridgerStatusText = "§eARMED"; break;
                case BRIDGING: bridgerStatusText = "§cBRIDGING"; break;
                default: bridgerStatusText = "§7IDLE"; break;
            }
            mc.fontRendererObj.drawStringWithShadow("Bridger: " + bridgerStatusText, 5, yOffset, 0xFFFFFF);
            
            if(bridgeController.getCurrentState() != BridgeController.State.IDLE) {
                 mc.fontRendererObj.drawStringWithShadow("§7Mode: " + ModConfig.currentBridgeMode.getDisplayName(), 5, yOffset + 10, 0xFFFFFF);
            }
        }
    }
}