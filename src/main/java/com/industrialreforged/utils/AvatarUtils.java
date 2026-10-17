package com.industrialreforged.utils;

import com.industrialreforged.render.avatar.IPlayerIcon;
import com.industrialreforged.render.avatar.PlayerIcon;
import com.industrialreforged.render.avatar.PlayerIconDefault;
import com.industrialreforged.render.avatar.PlayerIconGif;
import com.industrialreforged.render.avatar.PlayerIconSkin;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AvatarUtils {
   private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
   private static final HashMap<String, IPlayerIcon> BY_NAME = new HashMap<>();
   private static final HashMap<UUID, IPlayerIcon> BY_UUID = new HashMap<>();
   private static final Set<String> LOADING = new HashSet<>();
   private static final IPlayerIcon DEFAULT_ICON = new PlayerIconDefault();

   public static boolean isDownload(String username) {
      return LOADING.contains(username);
   }

   public static IPlayerIcon getAvatar(String username, UUID uuid) {
      IPlayerIcon playerIcon = BY_UUID.get(uuid);
      if (playerIcon != null) {
         return playerIcon;
      }

      if (LOADING.contains(username)) {
         return DEFAULT_ICON;
      }

 LOADING.add(username);
      loadAvatarAsync(username, uuid);
      return DEFAULT_ICON;
   }

   public static IPlayerIcon getAvatar(UUID uuid) {
      IPlayerIcon playerIcon = BY_UUID.get(uuid);
      if (playerIcon != null) {
         return playerIcon;
      }

      Optional<String> optional = PlayerUtils.getUsername(uuid);
      if (optional.isEmpty()) {
         return DEFAULT_ICON;
      }

      String username = optional.get();
      if (LOADING.contains(username)) {
         return DEFAULT_ICON;
      }

 LOADING.add(username);
      loadAvatarAsync(username, uuid);
      return DEFAULT_ICON;
   }

   public static IPlayerIcon getAvatar(String username) {
      IPlayerIcon playerIcon = BY_NAME.get(username);
      if (playerIcon != null) {
         return playerIcon;
      }

      Optional<UUID> optional = PlayerUtils.getUUID(username);
      if (optional.isEmpty()) {
         return DEFAULT_ICON;
      }

      if (LOADING.contains(username)) {
         return DEFAULT_ICON;
      }

 LOADING.add(username);
      loadAvatarAsync(username, optional.get());
      return DEFAULT_ICON;
   }

   private static void loadAvatarAsync(String username, UUID uuid) {
      CompletableFuture.runAsync(
         () -> {
            IPlayerIcon icon = null;

            try {
               String url = System.getenv("URL_WEB") + "/api/external/v1/files/get/avatar/" + uuid + "?inlineAnswer";
               HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
               connection.setRequestProperty("User-Agent", "Mozilla/5.0");
               if (connection.getResponseCode() == 200) {
                  InputStream in = connection.getInputStream();
                  String contentType = connection.getContentType();
                  if ("image/gif".equals(contentType)) {
                     readGif(in, username, uuid);
                  } else if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
                     byte[] header = new byte[8];
                     in.read(header);
                     if (header[0] == -119
                        && header[1] == 80
                        && header[2] == 78
                        && header[3] == 71
                        && header[4] == 13
                        && header[5] == 10
                        && header[6] == 26
                        && header[7] == 10) {
                        readImage(in, username, uuid);
                     } else if (header[0] == 71
                        && header[1] == 73
                        && header[2] == 70
                        && header[3] == 56
                        && (header[4] == 55 || header[4] == 57)
                        && header[5] == 97) {
                        readGif(in, username, uuid);
                     } else {
                        icon = new PlayerIconSkin(username, uuid);
                     }
                  } else {
                     readImage(in, username, uuid);
                  }
               } else {
                  icon = new PlayerIconSkin(username, uuid);
               }
            } catch (IOException e) {
               LOGGER.error("Reading avatar error", e);
               icon = new PlayerIconSkin(username, uuid);
            }

            IPlayerIcon finalIcon = icon;
            Minecraft.getInstance().execute(() -> {
               if (finalIcon != null) {
 BY_NAME.put(username, finalIcon);
 BY_UUID.put(uuid, finalIcon);
               }

 LOADING.remove(username);
            });
         }
      );
   }

   private static void readGif(InputStream in, String username, UUID uuid) throws IOException {
      IntList delays = new IntArrayList();
      List<BufferedImage> frames = new ArrayList<>();
      ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
      ImageInputStream imageStream = ImageIO.createImageInputStream(in);
      reader.setInput(imageStream);
      int width = -1;
      int height = -1;
      int numImages = reader.getNumImages(true);

      for (int i = 0; i < numImages; i++) {
         try {
            BufferedImage frame = reader.read(i);
            IIOMetadata metadata = reader.getImageMetadata(i);
            IIOMetadataNode root = (IIOMetadataNode)metadata.getAsTree(metadata.getNativeMetadataFormatName());
            IIOMetadataNode node = findGraphicControlExtension(root);
            String disposalMethod = node.getAttribute("disposalMethod");
            int delayTime = Integer.parseInt(node.getAttribute("delayTime"));
            if (i == 0) {
               width = frame.getWidth();
               height = frame.getHeight();
               frames.add(frame);
            } else if (disposalMethod.equals("doNotDispose")) {
               BufferedImage fullFrame = new BufferedImage(width, height, 2);
               Graphics2D g = fullFrame.createGraphics();
               g.drawImage(frames.get(frames.size() - 1), 0, 0, null);
               g.drawImage(frame, width - frame.getWidth(), height - frame.getHeight(), null);
               g.dispose();
               frames.add(fullFrame);
            } else if (frames.size() > 1 && disposalMethod.equals("restoreToPrevious")) {
               BufferedImage fullFrame = new BufferedImage(width, height, 2);
               Graphics2D g = fullFrame.createGraphics();
               g.drawImage(frames.get(frames.size() - 2), 0, 0, null);
               g.drawImage(frame, width - frame.getWidth(), height - frame.getHeight(), null);
               g.dispose();
               frames.add(fullFrame);
            } else {
               frames.add(frame);
            }

            delays.add(Math.max(delayTime, 1) * 10);
         } catch (ArrayIndexOutOfBoundsException var19) {
         }
      }

      Minecraft.getInstance().execute(() -> {
         try {
            if (!delays.isEmpty()) {
               PlayerIconGif icon = new PlayerIconGif(username, delays, frames);
 BY_NAME.put(username, icon);
 BY_UUID.put(uuid, icon);
            } else {
 BY_NAME.put(username, DEFAULT_ICON);
 BY_UUID.put(uuid, DEFAULT_ICON);
            }
         } catch (IOException e) {
 BY_NAME.put(username, DEFAULT_ICON);
 BY_UUID.put(uuid, DEFAULT_ICON);
         }
      });
   }

   private static IIOMetadataNode findGraphicControlExtension(IIOMetadataNode rootNode) {
      int numNodes = rootNode.getLength();

      for (int i = 0; i < numNodes; i++) {
         if (rootNode.item(i).getNodeName().equalsIgnoreCase("GraphicControlExtension")) {
            return (IIOMetadataNode)rootNode.item(i);
         }
      }

      return null;
   }

   private static void readImage(InputStream in, String username, UUID uuid) throws IOException {
      NativeImage image = ImageUtils.convert(ImageIO.read(in));
      Minecraft.getInstance().execute(() -> {
         DynamicTexture dynamicTexture = new DynamicTexture(image);
         ResourceLocation resourceLocation = Minecraft.getInstance().getTextureManager().register(uuid.toString(), dynamicTexture);
         PlayerIcon icon = new PlayerIcon(resourceLocation);
 BY_NAME.put(username, icon);
 BY_UUID.put(uuid, icon);
      });
   }
}
