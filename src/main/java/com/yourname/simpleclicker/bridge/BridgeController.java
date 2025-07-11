package com.yourname.simpleclicker.bridge;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.*;

public class BridgeController {

    public enum State { IDLE, ARMED, BRIDGING, STABILIZING }

    private static final Minecraft mc = Minecraft.getMinecraft();
    private State currentState = State.IDLE;
    private int blocksPlaced = 0;
    private int actionTimer = 0;

    public State getCurrentState() { return currentState; }
    public boolean isReady() { return isPlayerReady(); } // Новый метод для HUD

    public void onTick() {
        if (!ModConfig.bridgerEnabled || ModConfig.currentBridgeMode == BridgeMode.DISABLED || mc.currentScreen != null) {
            if (currentState != State.IDLE) disarm();
            return;
        }

        switch (currentState) {
            case ARMED:
                if (isPlayerReady()) { // Используем новый, надежный метод
                    ModConfig.isCameraLocked = true;
                    setPlayerRotation();
                } else {
                    disarm(); // Если игрок сошел с края, отключаемся
                }
                break;
            case BRIDGING:
                if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    stopBridging();
                    return;
                }
                executeBridgingLogic();
                actionTimer++;
                break;
            case STABILIZING:
                handleStabilizationState();
                break;
        }
    }

    private void handleStabilizationState() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        if (actionTimer < 10 && actionTimer % 3 == 0) {
             KeyBinding.onTick(mc.gameSettings.keyBindRight.getKeyCode());
        }
        
        actionTimer++;
        if (actionTimer > 20) {
            blocksPlaced = 0;
            currentState = State.BRIDGING;
        }
    }
    
    public void arm() {
        if (isPlayerReady()) { // Проверяем готовность перед активацией
            currentState = State.ARMED;
        }
    }

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
        if (currentState == State.BRIDGING || currentState == State.STABILIZING) {
            releaseAllKeys();
            if (isPlayerReady()) {
                currentState = State.ARMED;
            } else {
                disarm();
            }
        }
    }

    // --- РАДИКАЛЬНОЕ ИЗМЕНЕНИЕ: Замена isPlayerAtEdge на isPlayerReady ---
    private boolean isPlayerReady() {
        if (mc.thePlayer == null || !mc.thePlayer.onGround) return false;

        // Вектор взгляда игрока
        Vec3 lookVec = mc.thePlayer.getLookVec();
        // Вектор для проверки пространства перед игроком (вперед и немного вниз)
        Vec3 checkVec = lookVec.addVector(0, -0.2, 0).normalize();
        
        // Позиция глаз игрока
        Vec3 playerPos = mc.thePlayer.getPositionEyes(1.0f);
        // Конечная точка для трассировки луча (на 1.5 блока вперед)
        Vec3 targetPos = playerPos.add(checkVec.xCoord * 1.5, checkVec.yCoord * 1.5, checkVec.zCoord * 1.5);
        
        // Проверяем, есть ли блок впереди. Если есть (hit != null), то строить некуда. Нам нужен null.
        MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(playerPos, targetPos, false, false, true);
        
        // Условие: игрок на земле И перед ним пустое пространство.
        return mc.thePlayer.onGround && hit == null;
    }

    // ... (остальной код BridgeController остается без изменений) ...
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
                KeyBinding.setKeyBindState(back.getKeyCode(), true);
                KeyBinding.setKeyBindState(right.getKeyCode(), (actionTimer % 8) < 2);
                if (blocksPlaced > 0 && blocksPlaced % 8 == 0) {
                    actionTimer = 0;
                    currentState = State.STABILIZING;
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