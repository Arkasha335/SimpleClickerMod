package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.bridge.BridgeController;
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
    private boolean wasToggleKeyPressed = false;
    private boolean wasBridgerToggleKeyPressed = false;

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (SimpleClickerMod.openSettingsKey.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (mc.thePlayer == null || mc.currentScreen != null) return;

        if (ModConfig.bridgerBotActive && event.button == 1) {
            if(event.buttonstate) bridgeController.start();
            else bridgeController.stop();
            return;
        }

        if (!ModConfig.modEnabled) return;
        if (event.button == 0 && ModConfig.leftClickerEnabled) {
            if (event.buttonstate) { event.setCanceled(true); ModConfig.leftClickerActive = true; }
            else { ModConfig.leftClickerActive = false; }
        }
        if (event.button == 1 && ModConfig.rightClickerEnabled) {
            if (event.buttonstate) { event.setCanceled(true); ModConfig.rightClickerActive = true; }
            else { ModConfig.rightClickerActive = false; }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (SimpleClickerMod.toggleModKey.isKeyDown()) {
            if (!wasToggleKeyPressed) {
                ModConfig.modEnabled = !ModConfig.modEnabled;
                wasToggleKeyPressed = true;
            }
        } else {
            wasToggleKeyPressed = false;
        }
        
        if (SimpleClickerMod.toggleBridgerKey.isKeyDown()) {
            if (!wasBridgerToggleKeyPressed) {
                ModConfig.bridgerBotActive = !ModConfig.bridgerBotActive;
                wasBridgerToggleKeyPressed = true;
            }
        } else {
            wasBridgerToggleKeyPressed = false;
        }

        if(mc.thePlayer != null) {
            bridgeController.onTick();
        }

        if (mc.currentScreen != null) {
            if (ModConfig.leftClickerActive) ModConfig.leftClickerActive = false;
            if (ModConfig.rightClickerActive) ModConfig.rightClickerActive = false;
            if (ModConfig.bridgerBotActive) { bridgeController.stop(); ModConfig.bridgerBotActive = false; }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!ModConfig.hudEnabled || mc.gameSettings.showDebugInfo) return;
        
        int yOffset = 5;

        String clickerText = ModConfig.modEnabled ? "§aClicker" : "§cClicker";
        mc.fontRendererObj.drawStringWithShadow(clickerText, 5, yOffset, 0xFFFFFF);
        yOffset += 10;
        
        if(ModConfig.bridgerEnabled && ModConfig.currentBridgeMode != com.yourname.simpleclicker.bridge.BridgeMode.DISABLED) {
            String bridgerText = "Bridger: " + (ModConfig.bridgerBotActive ? "§aARMED" : "§eIDLE");
            String modeText = "§7(Mode: " + ModConfig.currentBridgeMode.getDisplayName() + ")";
            mc.fontRendererObj.drawStringWithShadow(bridgerText, 5, yOffset, 0xFFFFFF);
            mc.fontRendererObj.drawStringWithShadow(modeText, 5, yOffset + 10, 0xFFFFFF);
        }
    }
}