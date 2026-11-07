package com.thexyro.screenmod.net;

import com.thexyro.screenmod.ScreenMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Istemci -> sunucu: resim yukle (bos veri = resmi kaldir). */
public record UploadImagePayload(BlockPos pos, byte[] data) implements CustomPacketPayload {
	public static final Type<UploadImagePayload> ID = new Type<>(ScreenMod.id("upload_image"));
	public static final StreamCodec<FriendlyByteBuf, UploadImagePayload> CODEC =
			StreamCodec.ofMember(UploadImagePayload::write, UploadImagePayload::read);

	private static UploadImagePayload read(FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		byte[] data = buf.readByteArray();
		return new UploadImagePayload(pos, data);
	}

	private void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeByteArray(this.data);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
