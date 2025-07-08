package com.yourname.simpleclicker;

import com.yourname.simpleclicker.clicker.ClickerThread;
import com.yourname.simpleclicker.handlers.ClientEventHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class SimpleClickerMod {

    public static KeyBinding openSettingsKey;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // --- РЕГИСТРАЦИЯ ОБРАБОТЧИКОВ И КЛАВИШ ---
        ClientEventHandler handler = new ClientEventHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
        openSettingsKey = new KeyBinding("key.settings", Keyboard.KEY_RSHIFT, "key.categories.simpleclicker");
        ClientRegistry.registerKeyBinding(openSettingsKey);

        // --- ЗАПУСК НАШИХ ПОТОКОВ-КЛИКЕРОВ ---
        // Создаем и запускаем поток для ЛКМ (индекс кнопки 0)
        ClickerThread leftClicker = new ClickerThread(0);
        leftClicker.start();

        // Создаем и запускаем поток для ПКМ (индекс кнопки 1)
        ClickerThread rightClicker = new ClickerThread(1);
        rightClicker.start();
    }
}