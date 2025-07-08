package com.yourname.simpleclicker;

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
        // Создаем обработчик событий
        ClientEventHandler handler = new ClientEventHandler();

        // Регистрируем обработчик в шинах событий Forge
        // FMLCommonHandler для событий тиков и клавиатуры
        FMLCommonHandler.instance().bus().register(handler);
        // MinecraftForge.EVENT_BUS для других событий, если понадобятся
        MinecraftForge.EVENT_BUS.register(handler);

        // Создаем и регистрируем нашу кастомную клавишу
        // "key.settings" - ключ для локализации (названия)
        // Keyboard.KEY_RSHIFT - клавиша "Правый Shift"
        // "key.categories.gameplay" - категория в настройках управления
        openSettingsKey = new KeyBinding("key.settings", Keyboard.KEY_RSHIFT, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(openSettingsKey);
    }
}