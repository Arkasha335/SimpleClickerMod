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

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, clientSideOnly = true)
public class SimpleClickerMod {

    public static KeyBinding keyOpenSettings;
    public static KeyBinding keyToggleMod;
    public static KeyBinding keyToggleBridger;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientEventHandler handler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);

        String category = "key.categories.simpleclicker";

        keyOpenSettings = new KeyBinding("key.open_settings", Keyboard.KEY_RSHIFT, category);
        keyToggleMod = new KeyBinding("key.toggle_mod", Keyboard.KEY_LCONTROL, category);
        keyToggleBridger = new KeyBinding("key.toggle_bridger", Keyboard.KEY_G, category);

        ClientRegistry.registerKeyBinding(keyOpenSettings);
        ClientRegistry.registerKeyBinding(keyToggleMod);
        ClientRegistry.registerKeyBinding(keyToggleBridger);

        // --- ЗАПУСКАЕМ ПОТОКИ КЛИКЕРА ---
        new ClickerThread(0).start(); // Поток для ЛКМ
        new ClickerThread(1).start(); // Поток для ПКМ
    }
}