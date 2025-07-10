package com.yourname.simpleclicker;

import com.yourname.simpleclicker.bridge.BridgeController;
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
import org.lwjgl.input.Mouse;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class SimpleClickerMod {

    public static KeyBinding openSettingsKey;
    public static KeyBinding toggleModKey;
    public static KeyBinding toggleBridgerKey;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientEventHandler handler = new ClientEventHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);

        String category = "key.categories.simpleclicker"; 
        
        openSettingsKey = new KeyBinding("key.open_settings", Keyboard.KEY_RSHIFT, category);
        toggleModKey = new KeyBinding("key.toggle_mod", -98, category); 
        toggleBridgerKey = new KeyBinding("key.toggle_bridger", Keyboard.KEY_G, category);

        ClientRegistry.registerKeyBinding(openSettingsKey);
        ClientRegistry.registerKeyBinding(toggleModKey);
        ClientRegistry.registerKeyBinding(toggleBridgerKey);

        new ClickerThread(0).start();
        new ClickerThread(1).start();
    }
}