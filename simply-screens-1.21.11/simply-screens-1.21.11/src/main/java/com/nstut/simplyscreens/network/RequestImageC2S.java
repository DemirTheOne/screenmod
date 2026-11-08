package com.nstut.simplyscreens.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.nstut.simplyscreens.SimplyScreens;

public record RequestImageC2S(String name) implements CustomPacketPayload {
	public static final Type<RequestImageC2S> TYPE = new Type<>(SimplyScreens.id("request_image"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RequestImageC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, RequestImageC2S::name,
			RequestImageC2S::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
