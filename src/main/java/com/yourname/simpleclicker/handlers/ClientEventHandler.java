package com.yourname.simpleclicker.handlers;

import com.yourname.simpleclicker.SimpleClickerMod;
import com.yourname.simpleclicker.config.ModConfig;
import com.yourname.simpleclicker.gui.SettingsGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (SimpleClickerMod.openSettingsKey.isPressed()) {
            mc.displayGuiScreen(new SettingsGui());
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        // Мы используем игровой поток только для чтения состояния кнопок.
        // Это самый безопасный и производительный подход.
        if (event.phase == Phase.START && mc.thePlayer != null) {
            // Если игрок зажал ЛКМ, мы даем команду потоку-кликеру начать работу.
            ModConfig.leftClickerActive = mc.gameSettings.keyBindAttack.isKeyDown();
            // Если игрок зажал ПКМ, мы даем команду второму потоку-кликеру.
            ModConfig.rightClickerActive = mc.gameSettings.keyBindUseItem.isKeyDown();
        }
    }
}