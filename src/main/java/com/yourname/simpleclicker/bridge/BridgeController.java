package com.yourname.simpleclicker.bridge;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import java.awt.Color;

public class BridgeController {

    private enum State { IDLE, PREPARING, BRIDGING, STABILIZING, FINISHING }

    private static final Minecraft mc = Minecraft.getMinecraft();
    private State currentState = State.IDLE;
    private int blocksPlaced = 0;
    private int ticksSinceLastAction = 0;
    private int messageTicks = 0;
    private String hudMessage = "";
    private int messageColor = 0;

    public void onTick() {
        if (!ModConfig.bridgerEnabled || ModConfig.currentBridgeMode == BridgeMode.DISABLED) {
            if (currentState != State.IDLE) reset();
            return;
        }

        if (!ModConfig.bridgerBotActive) {
            if (currentState != State.IDLE) reset();
            return;
        }

        switch (currentState) {
            case PREPARING:
                if (isPlayerAtEdge()) {
                    setPlayerRotation();
                    currentState = State.BRIDGING;
                    blocksPlaced = 0;
                    ticksSinceLastAction = 0;
                } else {
                    displayMessage("§cError: Not at an edge!", 40);
                    reset();
                }
                break;
            case BRIDGING:
                executeBridgingLogic();
                blocksPlaced++;
                ticksSinceLastAction++;
                break;
            case STABILIZING:
                executeStabilizationLogic();
                ticksSinceLastAction++;
                break;
            default:
                break;
        }
    }

    private void executeBridgingLogic() {
        ModConfig.canPlaceBlocks = true;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);

        switch (ModConfig.currentBridgeMode) {
            case GODBRIDGE:
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true); // Диагональ
                if (blocksPlaced > 0 && blocksPlaced % 6 == 0) KeyBinding.onTick(mc.gameSettings.keyBindJump.getKeyCode());
                break;
            case NINJA:
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true);
                boolean shouldBeSneaking = (ticksSinceLastAction % 10) >= 4;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), shouldBeSneaking);
                break;
            case MOONWALK:
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
                boolean shouldStrafe = (ticksSinceLastAction % 7) < 2;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), shouldStrafe);
                if (blocksPlaced > 0 && blocksPlaced % 8 == 0) {
                    currentState = State.STABILIZING;
                    ticksSinceLastAction = 0;
                }
                break;
            case BREEZILY:
                boolean goLeft = (ticksSinceLastAction % 6) < 3;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), goLeft);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), !goLeft);
                break;
        }
    }

    private void executeStabilizationLogic() {
        ModConfig.canPlaceBlocks = false;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        
        if (ticksSinceLastAction == 2 || ticksSinceLastAction == 8) {
             KeyBinding.onTick(mc.gameSettings.keyBindLeft.getKeyCode());
        }
        
        if (ticksSinceLastAction > 20) {
            currentState = State.BRIDGING;
            ticksSinceLastAction = 0;
        }
    }

    private boolean isPlayerAtEdge() {
        if (mc.thePlayer == null) return false;
        EnumFacing playerFacing = mc.thePlayer.getHorizontalFacing();
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
        BlockPos blockInFront = playerPos.offset(playerFacing.getOpposite());
        return !mc.theWorld.isAirBlock(playerPos) && mc.theWorld.isAirBlock(blockInFront);
    }

    private void setPlayerRotation() {
        float yaw = mc.thePlayer.getHorizontalFacing().getHorizontalIndex() * 90;
        
        switch (ModConfig.currentBridgeMode) {
            case GODBRIDGE: mc.thePlayer.rotationPitch = 82.5f; mc.thePlayer.rotationYaw = yaw - 45f; break;
            case NINJA: mc.thePlayer.rotationPitch = 78.0f; mc.thePlayer.rotationYaw = yaw - 45f; break;
            case MOONWALK: mc.thePlayer.rotationPitch = 83.0f; mc.thePlayer.rotationYaw = yaw - 28.5f; break;
            case BREEZILY: mc.thePlayer.rotationPitch = 82.5f; mc.thePlayer.rotationYaw = yaw; break;
        }
    }

    public void start() {
        if (currentState == State.IDLE) {
            currentState = State.PREPARING;
        }
    }

    public void stop() {
        if (currentState != State.IDLE) {
            currentState = State.FINISHING;
            reset();
        }
    }

    private void reset() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        ModConfig.canPlaceBlocks = true;
        currentState = State.IDLE;
        blocksPlaced = 0;
    }

    public void onRenderOverlay() {
        if (messageTicks > 0) {
            int screenWidth = mc.displayWidth / 2;
            int screenHeight = mc.displayHeight / 2;
            mc.fontRendererObj.drawStringWithShadow(hudMessage, (screenWidth / 2) - (mc.fontRendererObj.getStringWidth(hudMessage) / 2), (screenHeight / 2) - 20, messageColor);
            messageTicks--;
        }
    }

    private void displayMessage(String message, int ticks) {
        this.hudMessage = message;
        this.messageTicks = ticks;
        this.messageColor = Color.WHITE.getRGB();
    }
}