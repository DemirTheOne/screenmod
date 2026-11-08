package com.nstut.simplyscreens.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.nstut.simplyscreens.SimplyScreens;

public record ImageChunkS2C(String name, int index, int total, byte[] data) implements CustomPacketPayload {
	public static final Type<ImageChunkS2C> TYPE = new Type<>(SimplyScreens.id("image_chunk"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ImageChunkS2C> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ImageChunkS2C::name,
			ByteBufCodecs.VAR_INT, ImageChunkS2C::index,
			ByteBufCodecs.VAR_INT, ImageChunkS2C::total,
			ByteBufCodecs.byteArray(ScreenNetworking.CHUNK_SIZE), ImageChunkS2C::data,
			ImageChunkS2C::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
