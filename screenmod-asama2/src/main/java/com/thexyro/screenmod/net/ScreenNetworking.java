package com.thexyro.screenmod.net;

import com.thexyro.screenmod.ScreenBlockEntity;
import java.util.Base64;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

/** Sunucu tarafi paket islemleri. */
public final class ScreenNetworking {
	/** Kabul edilen en buyuk JPEG boyutu (bayt). Base64 olunca NBT metin sinirina (65535) sigsin diye. */
	public static final int MAX_IMAGE_BYTES = 45000;

	private ScreenNetworking() { }

	public static void register() {
		// Buyuk paket: 32 KB'lik siradan sinirin ustune cikabilmek icin "large" olarak kayit
		PayloadTypeRegistry.playC2S().registerLarge(UploadImagePayload.ID, UploadImagePayload.CODEC, 100_000);
		PayloadTypeRegistry.playC2S().register(AspectPayload.ID, AspectPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(UploadImagePayload.ID, (payload, context) -> {
			ScreenBlockEntity be = find(context.player(), payload.pos());
			if (be == null) {
				return;
			}
			byte[] data = payload.data();
			if (data.length == 0) {
				be.setImage(null);
				return;
			}
			boolean looksLikeJpeg = data.length > 3 && (data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8;
			if (data.length > MAX_IMAGE_BYTES || !looksLikeJpeg) {
				return;
			}
			be.setImage(Base64.getEncoder().encodeToString(data));
		});

		ServerPlayNetworking.registerGlobalReceiver(AspectPayload.ID, (payload, context) -> {
			ScreenBlockEntity be = find(context.player(), payload.pos());
			if (be != null) {
				be.setKeepAspect(payload.keepAspect());
			}
		});
	}

	/** Oyuncu bloga yeterince yakinsa (8 blok) bloğun verisini dondurur. */
	private static ScreenBlockEntity find(ServerPlayer player, BlockPos pos) {
		if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) {
			return null;
		}
		if (player.level().getBlockEntity(pos) instanceof ScreenBlockEntity be) {
			return be;
		}
		return null;
	}
}
