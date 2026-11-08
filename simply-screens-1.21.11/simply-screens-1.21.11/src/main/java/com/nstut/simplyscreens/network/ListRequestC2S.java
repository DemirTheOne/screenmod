package com.nstut.simplyscreens.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.nstut.simplyscreens.SimplyScreens;

public record ListRequestC2S() implements CustomPacketPayload {
	public static final Type<ListRequestC2S> TYPE = new Type<>(SimplyScreens.id("list_request"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ListRequestC2S> CODEC = StreamCodec.unit(new ListRequestC2S());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
