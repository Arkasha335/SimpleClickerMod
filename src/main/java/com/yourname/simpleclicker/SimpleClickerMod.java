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

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, clientSideOnly = true)
public class SimpleClickerMod {

    public static KeyBinding keyOpenSettings;
    public static KeyBinding keyToggleMod;
    public static KeyBinding keyToggleBridger;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientEventHandler handler = new ClientEventHandler();
        // Регистрируем наш единый обработчик во всех необходимых шинах событий
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);

        // Категория для наших биндов в меню настроек
        String category = "key.categories.simpleclicker";

        // Инициализация клавиш
        keyOpenSettings = new KeyBinding("key.open_settings", Keyboard.KEY_RSHIFT, category);
        keyToggleMod = new KeyBinding("key.toggle_mod", Keyboard.KEY_LCONTROL, category); // Поставил на левый Ctrl, можно изменить
        keyToggleBridger = new KeyBinding("key.toggle_bridger", Keyboard.KEY_G, category);

        // Регистрация клавиш в игре
        ClientRegistry.registerKeyBinding(keyOpenSettings);
        ClientRegistry.registerKeyBinding(keyToggleMod);
        ClientRegistry.registerKeyBinding(keyToggleBridger);
    }
}