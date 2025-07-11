package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.bridge.BridgeMode;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
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
        if (ModConfig.modEnabled && !ModConfig.bridgerBotActive) {
            if (event.button == 0 && ModConfig.leftClickerEnabled) {
                ModConfig.leftClickerActive = event.buttonstate;
            }
            if (event.button == 1 && ModConfig.rightClickerEnabled) {
                ModConfig.rightClickerActive = event.buttonstate;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null) return;
        handleKeyToggles();
        
        if (mc.currentScreen != null) {
            // Если открыт GUI, сбрасываем все
            ModConfig.leftClickerActive = false;
            ModConfig.rightClickerActive = false;
            if (ModConfig.bridgerBotActive) {
                disableBridger();
            }
            return;
        }
        
        // --- НОВАЯ, ТУПАЯ И НАДЕЖНАЯ ЛОГИКА БРИДЖЕРА ---
        if (ModConfig.bridgerBotActive) {
            ModConfig.isCameraLocked = true;
            setPlayerRotation(); // Целимся
            
            // Если зажата ПКМ, держим кнопки для строительства
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                executeBridgingLogic();
            } else {
                // Если не зажата, отпускаем все, кроме шифта (если нужно)
                 releaseMovementKeys();
            }
        }
    }
    
    private void disableBridger() {
        ModConfig.bridgerBotActive = false;
        ModConfig.isCameraLocked = false;
        releaseMovementKeys();
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
                if(ModConfig.bridgerBotActive) {
                    disableBridger();
                } else {
                    ModConfig.bridgerBotActive = true;
                }
                wasToggleBridgerKeyPressed = true;
            }
        } else { wasToggleBridgerKeyPressed = false; }
    }

    // --- Логика бриджера, переехавшая сюда ---
    private void executeBridgingLogic() {
        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        if(ModConfig.currentBridgeMode == BridgeMode.NINJA || ModConfig.currentBridgeMode == BridgeMode.GODBRIDGE){
             KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
        }
    }
    
    private void releaseMovementKeys() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
    }

    private void setPlayerRotation() {
        EnumFacing facing = mc.thePlayer.getHorizontalFacing();
        float yaw = facing.getHorizontalIndex() * 90;
        
        if (ModConfig.currentBridgeMode == BridgeMode.GODBRIDGE || ModConfig.currentBridgeMode == BridgeMode.NINJA) {
            if (facing == EnumFacing.NORTH) yaw -= 45;
            if (facing == EnumFacing.SOUTH) yaw += 45;
            if (facing == EnumFacing.WEST) yaw -= 45;
            if (facing == EnumFacing.EAST) yaw += 45;
        }
        
        float pitch = 82.5f;
        if(ModConfig.currentBridgeMode == BridgeMode.NINJA) pitch = 79.0f;
        if(ModConfig.currentBridgeMode == BridgeMode.MOONWALK) { pitch = 83.5f; yaw += 28; }

        mc.thePlayer.rotationYaw = MathHelper.wrapAngleTo180_float(yaw);
        mc.thePlayer.rotationPitch = pitch;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!ModConfig.hudEnabled || mc.gameSettings.showDebugInfo) return;
        int yOffset = 5;
        mc.fontRendererObj.drawStringWithShadow("Clicker: " + (ModConfig.modEnabled ? "§aON" : "§cOFF"), 5, yOffset, 0xFFFFFF);
        yOffset += 10;
        
        if (ModConfig.bridgerEnabled && ModConfig.currentBridgeMode != BridgeMode.DISABLED) {
            String bridgerStatusText = ModConfig.bridgerBotActive ? "§eARMED" : "§7IDLE";
            mc.fontRendererObj.drawStringWithShadow("Bridger: " + bridgerStatusText, 5, yOffset, 0xFFFFFF);
        }
    }
}