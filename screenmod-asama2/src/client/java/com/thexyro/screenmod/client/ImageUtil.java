package com.thexyro.screenmod.client;

import com.thexyro.screenmod.net.ScreenNetworking;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/** Secilen PNG/JPG dosyasini sunucuya gonderilebilecek kucuklukte bir JPEG'e cevirir. */
public final class ImageUtil {
	private static final int[] MAX_SIDES = {384, 320, 256, 192, 128, 96};
	private static final float[] QUALITIES = {0.85f, 0.7f, 0.55f, 0.4f};

	private ImageUtil() { }

	public static byte[] prepareJpeg(Path file) throws IOException {
		BufferedImage src = ImageIO.read(file.toFile());
		if (src == null) {
			throw new IOException("Not a PNG/JPG image");
		}
		for (int side : MAX_SIDES) {
			double scale = Math.min(1.0, (double) side / Math.max(src.getWidth(), src.getHeight()));
			int w = Math.max(1, (int) Math.round(src.getWidth() * scale));
			int h = Math.max(1, (int) Math.round(src.getHeight() * scale));

			BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = out.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, w, h);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(src, 0, 0, w, h, null);
			g.dispose();

			for (float quality : QUALITIES) {
				byte[] jpeg = encodeJpeg(out, quality);
				if (jpeg.length <= ScreenNetworking.MAX_IMAGE_BYTES) {
					return jpeg;
				}
			}
		}
		throw new IOException("Image is too complex to shrink");
	}

	private static byte[] encodeJpeg(BufferedImage image, float quality) throws IOException {
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		try {
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			try (ImageOutputStream ios = ImageIO.createImageOutputStream(bytes)) {
				writer.setOutput(ios);
				writer.write(null, new IIOImage(image, null, null), param);
			}
			return bytes.toByteArray();
		} finally {
			writer.dispose();
		}
	}
}
