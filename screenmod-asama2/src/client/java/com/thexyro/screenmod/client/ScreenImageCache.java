package com.thexyro.screenmod.client;

import com.thexyro.screenmod.ScreenMod;
import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

/** base64 JPEG'i oyun texture'una cevirir ve hatirlar (her karede tekrar cozmesin diye). */
public final class ScreenImageCache {
	public record Entry(Identifier id, int width, int height) { }

	private static final Map<String, Entry> CACHE = new HashMap<>();
	private static final Set<String> FAILED = new HashSet<>();

	private ScreenImageCache() { }

	public static Entry get(String base64) {
		String key = Integer.toHexString(base64.hashCode()) + "_" + base64.length();
		Entry cached = CACHE.get(key);
		if (cached != null) {
			return cached;
		}
		if (FAILED.contains(key)) {
			return null;
		}
		try {
			byte[] jpeg = Base64.getDecoder().decode(base64);
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(jpeg));
			if (image == null) {
				throw new IOException("decode failed");
			}
			ByteArrayOutputStream png = new ByteArrayOutputStream();
			ImageIO.write(image, "png", png);
			NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(png.toByteArray()));

			Identifier id = Identifier.fromNamespaceAndPath(ScreenMod.MOD_ID, "screen_img/" + key);
			Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(id::toString, nativeImage));
			Entry entry = new Entry(id, nativeImage.getWidth(), nativeImage.getHeight());
			CACHE.put(key, entry);
			return entry;
		} catch (Exception e) {
			FAILED.add(key);
			return null;
		}
	}
}
