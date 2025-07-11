package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.bridge.BridgeController;
import com.yourname.simpleclicker.bridge.BridgeMode;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final BridgeController bridgeController = new BridgeController();
    private boolean wasToggleModKeyPressed = false;
    private boolean wasToggleBridgerKeyPressed = false;

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

        if (mc.currentScreen != null) return;
        
        // Управление флагами для ClickerThread
        if (ModConfig.modEnabled && bridgeController.getCurrentState() == BridgeController.State.IDLE) {
            if (event.button == 0 && ModConfig.leftClickerEnabled) {
                ModConfig.leftClickerActive = event.buttonstate;
            }
            if (event.button == 1 && ModConfig.rightClickerEnabled) {
                ModConfig.rightClickerActive = event.buttonstate;
            }
        }
        
        // Управление Bridger
        if (ModConfig.bridgerEnabled && event.button == 1) {
             if (bridgeController.getCurrentState() == BridgeController.State.ARMED && event.buttonstate) {
                bridgeController.startBridging();
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null) return;
        handleKeyToggles();
        bridgeController.onTick();
        
        // Если открыт GUI, сбрасываем все состояния
        if (mc.currentScreen != null) {
            ModConfig.leftClickerActive = false;
            ModConfig.rightClickerActive = false;
            if (bridgeController.getCurrentState() != BridgeController.State.IDLE) {
                bridgeController.disarm();
            }
        }
    }

    private void handleKeyToggles() {
        if (SimpleClickerMod.keyToggleMod.isKeyDown()) {
            if (!wasToggleModKeyPressed) {
                ModConfig.modEnabled = !ModConfig.modEnabled;
                wasToggleModKeyPressed = true;
            }
        } else { wasToggleModKeyPressed = false; }

        if (SimpleClickerMod.keyToggleBridger.isKeyDown()) {
            if (!wasToggleBridgerKeyPressed) {
                if (bridgeController.getCurrentState() == BridgeController.State.IDLE) {
                    bridgeController.arm();
                } else {
                    bridgeController.disarm();
                }
                wasToggleBridgerKeyPressed = true;
            }
        } else { wasToggleBridgerKeyPressed = false; }
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