package com.yourname.simpleclicker.bridge;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class BridgeController {

    public enum State {
        IDLE,       // Модуль неактивен (выключен или не выбран режим)
        ARMED,      // Модуль включен, ждет на краю блока, целится
        BRIDGING,   // Игрок зажал мышь, идет процесс строительства
        STABILIZING // Фаза стабилизации (для Moonwalk)
    }

    private static final Minecraft mc = Minecraft.getMinecraft();
    private State currentState = State.IDLE;
    private int blocksPlaced = 0;
    private int actionTimer = 0; // Таймер для выполнения последовательности действий

    public State getCurrentState() {
        return currentState;
    }

    // Главный метод, вызывается каждый тик
    public void onTick() {
        if (!ModConfig.bridgerEnabled || ModConfig.currentBridgeMode == BridgeMode.DISABLED || mc.currentScreen != null) {
            if (currentState != State.IDLE) {
                disarm();
            }
            return;
        }

        // Логика состояний
        switch (currentState) {
            case ARMED:
                handleArmedState();
                break;
            case BRIDGING:
                handleBridgingState();
                break;
            case STABILIZING:
                handleStabilizationState();
                break;
        }
    }

    // Логика состояния "ВООРУЖЕН"
    private void handleArmedState() {
        if (isPlayerAtEdge()) {
            ModConfig.isCameraLocked = true; // Блокируем камеру
            setPlayerRotation(); // Нацеливаемся
        } else {
            disarm(); // Если игрок сошел с края, отключаемся
        }
    }

    // Логика состояния "СТРОИТЕЛЬСТВО"
    private void handleBridgingState() {
        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) { // Если отпустили кнопку мыши
            stopBridging();
            return;
        }
        executeBridgingLogic();
        actionTimer++;
    }

    // Логика состояния "СТАБИЛИЗАЦИЯ" (для Мунволка)
    private void handleStabilizationState() {
        // Имитируем медленное движение до края
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        if (actionTimer < 10 && actionTimer % 3 == 0) { // Медленные тапы вправо
             KeyBinding.onTick(mc.gameSettings.keyBindRight.getKeyCode());
        }
        
        actionTimer++;
        if (actionTimer > 20) { // После 1 секунды стабилизации
            blocksPlaced = 0; // Сбрасываем счетчик для следующей паузы
            currentState = State.BRIDGING;
        }
    }
    
    // Включение режима "ВООРУЖЕН"
    public void arm() {
        if (isPlayerAtEdge()) {
            currentState = State.ARMED;
        }
    }

    // Выключение режима
    public void disarm() {
        if (currentState != State.IDLE) {
            releaseAllKeys();
            ModConfig.isCameraLocked = false;
            currentState = State.IDLE;
        }
    }

    // Начать строительство (вызывается извне)
    public void startBridging() {
        if (currentState == State.ARMED) {
            blocksPlaced = 0;
            actionTimer = 0;
            currentState = State.BRIDGING;
        }
    }

    // Закончить строительство (вызывается извне)
    public void stopBridging() {
        if (currentState == State.BRIDGING || currentState == State.STABILIZING) {
            releaseAllKeys();
            // Возвращаемся в состояние ARMED, если все еще на краю блока
            if (isPlayerAtEdge()) {
                currentState = State.ARMED;
            } else {
                disarm();
            }
        }
    }

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
                     KeyBinding.onTick(jump.getKeyCode()); // Короткое нажатие прыжка
                }
                break;
            case NINJA:
                KeyBinding.setKeyBindState(back.getKeyCode(), true);
                KeyBinding.setKeyBindState(right.getKeyCode(), true);
                // Тайминги для шифта: 4 тика зажат, 6 тиков отжат (примерно)
                boolean shouldBeSneaking = (actionTimer % 10) > 4; 
                KeyBinding.setKeyBindState(sneak.getKeyCode(), shouldBeSneaking);
                break;
            case MOONWALK:
                KeyBinding.setKeyBindState(back.getKeyCode(), true);
                // Тапаем вправо
                KeyBinding.setKeyBindState(right.getKeyCode(), (actionTimer % 8) < 2);
                if (blocksPlaced > 0 && blocksPlaced % 8 == 0) {
                    actionTimer = 0;
                    currentState = State.STABILIZING;
                }
                break;
            case BREEZILY:
                KeyBinding.setKeyBindState(back.getKeyCode(), true);
                // Поочередные стрейфы
                boolean goLeft = (actionTimer / 4) % 2 == 0;
                KeyBinding.setKeyBindState(left.getKeyCode(), goLeft);
                KeyBinding.setKeyBindState(right.getKeyCode(), !goLeft);
                break;
        }
        blocksPlaced++;
    }

    private boolean isPlayerAtEdge() {
        if (mc.thePlayer == null || !mc.thePlayer.onGround) return false;

        EnumFacing playerFacing = mc.thePlayer.getHorizontalFacing();
        // Проверяем блок прямо под ногами и блок перед игроком (в направлении взгляда)
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
        BlockPos blockInFront = playerPos.offset(playerFacing);

        return mc.theWorld.isBlockNormalCube(playerPos, false) && mc.theWorld.isAirBlock(blockInFront);
    }

    private void setPlayerRotation() {
        EnumFacing facing = mc.thePlayer.getHorizontalFacing();
        float yaw = facing.getHorizontalIndex() * 90;
        
        // Корректируем yaw для диагональных видов строительства
        if (ModConfig.currentBridgeMode == BridgeMode.GODBRIDGE || ModConfig.currentBridgeMode == BridgeMode.NINJA) {
            // Умная коррекция диагонали в зависимости от направления
            if (facing == EnumFacing.NORTH) yaw -= 45;
            if (facing == EnumFacing.SOUTH) yaw += 45;
            if (facing == EnumFacing.WEST) yaw -= 45;
            if (facing == EnumFacing.EAST) yaw += 45;
        }
        
        float pitch = 82.5f; // Базовый питч
        switch(ModConfig.currentBridgeMode) {
            case NINJA: pitch = 79.0f; break;
            case MOONWALK: pitch = 83.5f; yaw += 28; break; // Угол для мунволка
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