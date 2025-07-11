package com.yourname.simpleclicker.clicker;

import com.yourname.simpleclicker.config.ModConfig;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class ClickerThread extends Thread {
   private final int button;
   private final Random random = new Random();
   private final Minecraft mc = Minecraft.getMinecraft(); // Используем func_71410_x() неявно

   public ClickerThread(int button) {
      super("ClickerThread-" + (button == 0 ? "Left" : "Right"));
      this.button = button;
   }

   public void run() {
      while(true) {
         try {
            // Проверяем, активен ли кликер
            boolean isActive = this.button == 0 ? ModConfig.leftClickerActive : ModConfig.rightClickerActive;
            boolean isEnabled = this.button == 0 ? ModConfig.leftClickerEnabled : ModConfig.rightClickerEnabled;
            
            if (ModConfig.modEnabled && isActive && isEnabled) {
               // Получаем настройки
               double cps = this.button == 0 ? ModConfig.leftCps : ModConfig.rightCps;
               double randomization = this.button == 0 ? ModConfig.leftRandomization : ModConfig.rightRandomization;
               
               // Выполняем клик и спим
               this.performClick();
               Thread.sleep(this.calculateDelay(cps, randomization));
            } else {
               // Если неактивен, спим дольше
               Thread.sleep(100L);
            }
         } catch (Exception ignored) {
            // Игнорируем ошибки, чтобы поток не падал
         }
      }
   }

   private void performClick() {
      KeyBinding key;
      // Используем правильные обфусцированные поля для keybind'ов
      if (this.button == 0) {
         key = this.mc.gameSettings.keyBindAttack; // mc.field_71474_y.field_74312_F
      } else {
         key = this.mc.gameSettings.keyBindUseItem; // mc.field_71474_y.field_74313_G
      }

      // Используем правильный обфусцированный метод onTick
      KeyBinding.onTick(key.getKeyCode()); // KeyBinding.func_74507_a(key.func_151463_i())
   }

   private long calculateDelay(double cps, double randomization) {
      if (cps <= 0.0) {
         return 1000L;
      }
      long baseDelay = (long)(1000.0 / cps);
      if (randomization > 0.0) {
         long randomOffset = (long)((this.random.nextDouble() - 0.5) * (double)baseDelay * randomization);
         return Math.max(5L, baseDelay + randomOffset);
      }
      return Math.max(5L, baseDelay);
   }
}