package com.industrialreforged.debug.bridge;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Client half of the dev bridge.  Only referenced when a screenshot is asked
 * for, so dedicated servers never touch it.
 */
public final class DevBridgeClient {
   private DevBridgeClient() {
   }

   /** Saves a screenshot into run/screenshots and returns its absolute path. */
   public static String screenshot(String name) throws Exception {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft == null) {
         throw new IllegalStateException("no client");
      }

      String fileName = name.endsWith(".png") ? name : name + ".png";
      CountDownLatch latch = new CountDownLatch(1);
      String[] message = new String[1];

      minecraft.execute(() -> {
         try {
            Screenshot.grab(minecraft.gameDirectory, fileName, minecraft.getMainRenderTarget(), component -> {
               message[0] = component.getString();
               latch.countDown();
            });
         } catch (Throwable t) {
            message[0] = String.valueOf(t);
            latch.countDown();
         }
      });

      if (!latch.await(15L, TimeUnit.SECONDS)) {
         throw new IllegalStateException("screenshot timed out");
      }

      File file = new File(new File(minecraft.gameDirectory, "screenshots"), fileName);
      return file.getAbsolutePath() + (message[0] != null && !message[0].isBlank() ? " | " + message[0] : "");
   }

   /**
    * NBT блок-сущности на стороне КЛИЕНТА.
    * <p>
    * Нужен, чтобы правки синхронизации можно было проверять автоматически. Серверная
    * копия block entity всегда правильная, а клиентская может остаться пустой — тогда
    * экран рисует нули, и это не видно ни в логе, ни в тестах на сервере. Именно так
    * выглядели шесть несинхронизированных значений GUI, найденных в сессии 35.
    *
    * @return SNBT клиентской копии или строка-объяснение, если копии нет
    */
   public static String clientBlockEntity(int x, int y, int z) throws Exception {
      CountDownLatch latch = new CountDownLatch(1);
      String[] result = new String[1];

      Minecraft.getInstance().execute(() -> {
         try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
               result[0] = "<нет мира>";
               return;
            }

            BlockEntity entity = minecraft.level.getBlockEntity(new BlockPos(x, y, z));
            if (entity == null) {
               result[0] = "<нет блок-сущности>";
               return;
            }

            // NBT показывает только персистентные поля. Экран же рисует и неперсистентные
            // (production, progress, skyLight, redstoneMode), поэтому дописываем их
            // рефлексией — иначе «клиент слеп» невозможно заметить.
            StringBuilder sb = new StringBuilder();
            sb.append("nbt=").append(entity.saveWithoutMetadata(minecraft.level.registryAccess()));
            sb.append(" fields=");
            boolean first = true;
            for (Class<?> type = entity.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
               for (java.lang.reflect.Field field : type.getDeclaredFields()) {
                  if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                     continue;
                  }

                  Class<?> kind = field.getType();
                  boolean simple = kind.isPrimitive() || kind == String.class
                     || Number.class.isAssignableFrom(kind) || kind.isEnum();
                  // баки и накопители тоже нужны: синхронизация жидкостей в GUI
                  // ломается так же незаметно, как синхронизация чисел
                  boolean containerLike = kind.getSimpleName().contains("Tank")
                     || kind.getSimpleName().contains("Storage");
                  if (!simple && !containerLike) {
                     continue;
                  }

                  try {
                     field.setAccessible(true);
                     Object value = field.get(entity);
                     if (value == null) {
                        continue;
                     }

                     if (!first) {
                        sb.append(", ");
                     }

                     sb.append(field.getName()).append("=").append(value);
                     first = false;
                  } catch (Throwable ignored) {
                     // поле недоступно — молча пропускаем
                  }
               }
            }

            result[0] = sb.toString();
         } catch (Throwable t) {
            result[0] = "<ошибка: " + t + ">";
         } finally {
            latch.countDown();
         }
      });

      if (!latch.await(10L, TimeUnit.SECONDS)) {
         throw new IllegalStateException("клиент не ответил");
      }

      return result[0] == null ? "<пусто>" : result[0];
   }
}
