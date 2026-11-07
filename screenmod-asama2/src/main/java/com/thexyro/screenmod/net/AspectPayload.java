package com.thexyro.screenmod.net;

import com.thexyro.screenmod.ScreenMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Istemci -> sunucu: "Maintain Aspect Ratio" ayari. */
public record AspectPayload(BlockPos pos, boolean keepAspect) implements CustomPacketPayload {
	public static final Type<AspectPayload> ID = new Type<>(ScreenMod.id("aspect"));
	public static final StreamCodec<FriendlyByteBuf, AspectPayload> CODEC =
			StreamCodec.ofMember(AspectPayload::write, AspectPayload::read);

	private static AspectPayload read(FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		boolean keep = buf.readBoolean();
		return new AspectPayload(pos, keep);
	}

	private void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeBoolean(this.keepAspect);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
