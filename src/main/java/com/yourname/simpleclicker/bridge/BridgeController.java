package com.yourname.simpleclicker.bridge;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class BridgeController {

    // --- УПРОЩЕНО: Убрали лишние состояния ---
    public enum State { IDLE, ARMED, BRIDGING }

    private static final Minecraft mc = Minecraft.getMinecraft();
    private State currentState = State.IDLE;
    private int blocksPlaced = 0;
    private int actionTimer = 0;

    public State getCurrentState() { return currentState; }

    public void onTick() {
        if (!ModConfig.bridgerEnabled || ModConfig.currentBridgeMode == BridgeMode.DISABLED || mc.currentScreen != null) {
            if (currentState != State.IDLE) disarm();
            return;
        }

        switch (currentState) {
            case ARMED:
                // --- НОВАЯ ЛОГИКА: Просто целится и блокирует камеру. Никаких проверок. ---
                ModConfig.isCameraLocked = true;
                setPlayerRotation();
                break;
            case BRIDGING:
                // Если отпустили кнопку, прекращаем строить
                if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    stopBridging();
                    return;
                }
                executeBridgingLogic();
                actionTimer++;
                break;
        }
    }
    
    // --- УПРОЩЕНО: Просто переходит в режим ARMED ---
    public void arm() {
        currentState = State.ARMED;
    }

    // --- УПРОЩЕНО: Просто отключает все ---
    public void disarm() {
        if (currentState != State.IDLE) {
            releaseAllKeys();
            ModConfig.isCameraLocked = false;
            currentState = State.IDLE;
        }
    }

    public void startBridging() {
        if (currentState == State.ARMED) {
            blocksPlaced = 0;
            actionTimer = 0;
            currentState = State.BRIDGING;
        }
    }

    public void stopBridging() {
        if (currentState == State.BRIDGING) {
           disarm(); // Просто полностью отключаемся после строительства
        }
    }
    
    // --- УДАЛЕНО: Метод isPlayerReady() больше не нужен, мы ему не доверяем ---

    // ... (executeBridgingLogic и setPlayerRotation остаются без изменений) ...
    private void executeBridgingLogic() {
        KeyBinding sneak = mc.gameSettings.keyBindSneak;
        KeyBinding back = mc.gameSettings.keyBindBack;
        KeyBinding right = mc.gameSettings.keyBindRight;
        KeyBinding left = mc.gameSettings.keyBindLeft;
        KeyBinding jump = mc.gameSettings.keyBindJump;

        switch (ModConfig.currentBridgeMode) {
            case GODBRIDGE:
                KeyBinding.setKeyBindState(back.getKeyCode(), true);
                KeyBinding.setKeyBindState(right.getKeyCode(), true);
                if (blocksPlaced > 0 && blocksPlaced % 6 == 0) {
                     KeyBinding.onTick(jump.getKeyCode());
                }
                break;
            case NINJA:
                KeyBinding.setKeyBindState(back.getKeyCode(), true);
                KeyBinding.setKeyBindState(right.getKeyCode(), true);
                boolean shouldBeSneaking = (actionTimer % 10) > 4; 
                KeyBinding.setKeyBindState(sneak.getKeyCode(), shouldBeSneaking);
                break;
            case MOONWALK:
                // Для мунволка убрана стабилизация для простоты и надежности
                KeyBinding.setKeyBindState(back.getKeyCode(), true);
                KeyBinding.setKeyBindState(right.getKeyCode(), (actionTimer % 8) < 2);
                if (blocksPlaced > 0 && blocksPlaced % 8 == 0) {
                    // Просто короткая пауза на шифте
                    KeyBinding.onTick(sneak.getKeyCode());
                }
                break;
            case BREEZILY:
                KeyBinding.setKeyBindState(back.getKeyCode(), true);
                boolean goLeft = (actionTimer / 4) % 2 == 0;
                KeyBinding.setKeyBindState(left.getKeyCode(), goLeft);
                KeyBinding.setKeyBindState(right.getKeyCode(), !goLeft);
                break;
        }
        blocksPlaced++;
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
        switch(ModConfig.currentBridgeMode) {
            case NINJA: pitch = 79.0f; break;
            case MOONWALK: pitch = 83.5f; yaw += 28; break;
        }

        mc.thePlayer.rotationYaw = MathHelper.wrapAngleTo180_float(yaw);
        mc.thePlayer.rotationPitch = pitch;
    }
    
    private void releaseAllKeys() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }
}