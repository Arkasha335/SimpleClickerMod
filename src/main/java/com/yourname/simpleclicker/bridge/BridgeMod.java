package com.yourname.simpleclicker.bridge;

public enum BridgeMode {
    DISABLED("Disabled"),
    GODBRIDGE("Godbridge"),
    NINJA("Ninja"),
    MOONWALK("Moonwalk"),
    BREEZILY("Breezily");

    private final String displayName;

    BridgeMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Метод для циклического переключения режимов
    public BridgeMode getNext() {
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }
}```

##### **Файл: `bridge/BridgeController.java` (НОВЫЙ)**

Это "мозг" нашего строительного бота. Самый сложный и важный новый класс.

**Путь:** `src/main/java/com/yourname/simpleclicker/bridge/BridgeController.java`
```java
package com.yourname.simpleclicker.bridge;

import com.yourname.simpleclicker.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class BridgeController {

    private enum State { IDLE, PREPARING, BRIDGING, STABILIZING, FINISHING }

    private static final Minecraft mc = Minecraft.getMinecraft();
    private State currentState = State.IDLE;
    private int blocksPlaced = 0;
    private int ticksSinceLastAction = 0;

    public void onTick() {
        if (!ModConfig.bridgerEnabled || ModConfig.currentBridgeMode == BridgeMode.DISABLED) {
            if(currentState != State.IDLE) reset();
            return;
        }

        if (!ModConfig.bridgerBotActive) {
            if(currentState != State.IDLE) reset();
            return;
        }

        switch (currentState) {
            case IDLE:
                // Ждем, пока триггер (в MouseEvent) переведет нас в PREPARING
                break;
            case PREPARING:
                if (isPlayerAtEdge()) {
                    setPlayerRotation();
                    currentState = State.BRIDGING;
                    blocksPlaced = 0;
                    ticksSinceLastAction = 0;
                } else {
                    // TODO: Вывести сообщение об ошибке
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
            case FINISHING:
                reset();
                break;
        }
    }

    private void executeBridgingLogic() {
        ModConfig.canPlaceBlocks = true;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);

        switch (ModConfig.currentBridgeMode) {
            case GODBRIDGE:
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
                if (blocksPlaced > 0 && blocksPlaced % 6 == 0) {
                    KeyBinding.onTick(mc.gameSettings.keyBindJump.getKeyCode()); // Прыжок
                }
                break;
            case NINJA:
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
                // Тайминговый шифт: отпускаем на 6 тиков (0.3с), зажимаем на 4 тика (0.2с)
                boolean shouldBeSneaking = (ticksSinceLastAction % 10) > 3;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), shouldBeSneaking);
                break;
            case MOONWALK:
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
                // Тапаем стрейф: 2 тика нажато, 5 тиков отпущено
                boolean shouldStrafe = (ticksSinceLastAction % 7) < 2;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), shouldStrafe); // Предположим, что Yaw настроен для стрейфа влево
                if (blocksPlaced > 0 && blocksPlaced % 8 == 0) {
                    currentState = State.STABILIZING;
                    ticksSinceLastAction = 0;
                }
                break;
            case BREEZILY:
                // Быстрые поочередные стрейфы: 3 тика влево, 3 тика вправо
                boolean goLeft = (ticksSinceLastAction % 6) < 3;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), goLeft);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), !goLeft);
                break;
        }
    }
    
    private void executeStabilizationLogic() {
        ModConfig.canPlaceBlocks = false; // Блокируем кликер
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        
        // Два медленных тапа для коррекции
        if(ticksSinceLastAction == 2 || ticksSinceLastAction == 8) {
             KeyBinding.onTick(mc.gameSettings.keyBindLeft.getKeyCode());
        }
        
        // Задержка 20 тиков (1 секунда) на стабилизацию
        if(ticksSinceLastAction > 20) {
            currentState = State.BRIDGING;
            ticksSinceLastAction = 0;
        }
    }

    private boolean isPlayerAtEdge() {
        EnumFacing playerFacing = mc.thePlayer.getHorizontalFacing();
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
        BlockPos blockInFront = playerPos.offset(playerFacing.getOpposite());
        return mc.theWorld.isAirBlock(blockInFront);
    }
    
    private void setPlayerRotation() {
        switch (ModConfig.currentBridgeMode) {
            case GODBRIDGE: mc.thePlayer.rotationPitch = 82.5f; mc.thePlayer.rotationYaw += 45f; break;
            case NINJA: mc.thePlayer.rotationPitch = 78.0f; mc.thePlayer.rotationYaw += 45f; break;
            case MOONWALK: mc.thePlayer.rotationPitch = 83.0f; mc.thePlayer.rotationYaw += 28.5f; break;
            case BREEZILY: mc.thePlayer.rotationPitch = 82.5f; break;
        }
    }

    public void start() {
        if (currentState == State.IDLE) {
            currentState = State.PREPARING;
        }
    }

    public void stop() {
        currentState = State.FINISHING;
    }

    private void reset() {
        // Отпускаем все клавиши, которыми управляли
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        ModConfig.canPlaceBlocks = true;
        currentState = State.IDLE;
        blocksPlaced = 0;
    }
}```

---

#### **Пакет `handlers`**

##### **Файл: `ClientEventHandler.java` (Обновлен)**

Интегрируем новый контроллер и добавляем логику триггера.

**Путь:** `src/main/java/com/yourname/simpleclicker/handlers/ClientEventHandler.java`
```java
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
    private final BridgeController bridgeController = new BridgeController(); // <-- Создаем экземпляр нашего бота
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

        // Триггер для Bridger
        if (ModConfig.bridgerBotActive && event.button == 1) { // Если бот "вооружен" и нажат ПКМ
            if(event.buttonstate) bridgeController.start();
            else bridgeController.stop();
            return; // Важно: не даем автокликеру перехватить это событие
        }

        // Логика автокликера (остается без изменений)
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

        // --- Обработка клавиш ---
        // Глобальный переключатель
        if (SimpleClickerMod.toggleModKey.isKeyDown()) {
            if (!wasToggleKeyPressed) {
                ModConfig.modEnabled = !ModConfig.modEnabled;
                wasToggleKeyPressed = true;
            }
        } else {
            wasToggleKeyPressed = false;
        }
        
        // Переключатель "вооружения" Bridger
        if (SimpleClickerMod.toggleBridgerKey.isKeyDown()) {
            if (!wasBridgerToggleKeyPressed) {
                ModConfig.bridgerBotActive = !ModConfig.bridgerBotActive;
                wasBridgerToggleKeyPressed = true;
            }
        } else {
            wasBridgerToggleKeyPressed = false;
        }

        // --- Логика бота ---
        if(mc.thePlayer != null) {
            bridgeController.onTick();
        }

        // Предохранитель для GUI
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

        // HUD для кликера
        String clickerText = ModConfig.modEnabled ? "§aClicker" : "§cClicker";
        mc.fontRendererObj.drawStringWithShadow(clickerText, 5, yOffset, 0xFFFFFF);
        yOffset += 10;
        
        // HUD для Bridger
        if(ModConfig.bridgerEnabled && ModConfig.currentBridgeMode != com.yourname.simpleclicker.bridge.BridgeMode.DISABLED) {
            String bridgerText = "Bridger: " + (ModConfig.bridgerBotActive ? "§aARMED" : "§eIDLE");
            String modeText = "§7(Mode: " + ModConfig.currentBridgeMode.getDisplayName() + ")";
            mc.fontRendererObj.drawStringWithShadow(bridgerText, 5, yOffset, 0xFFFFFF);
            mc.fontRendererObj.drawStringWithShadow(modeText, 5, yOffset + 10, 0xFFFFFF);
        }
    }
}