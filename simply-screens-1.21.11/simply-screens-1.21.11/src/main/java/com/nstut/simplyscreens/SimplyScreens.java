package com.nstut.simplyscreens;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.nstut.simplyscreens.network.ScreenNetworking;

public class SimplyScreens implements ModInitializer {
	public static final String MOD_ID = "simply_screens";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ScreenNetworking.registerPayloads();
		ScreenNetworking.registerServerReceivers();

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
				.register(entries -> entries.accept(ModBlocks.SCREEN.asItem()));

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> ScreenBlockEntity.clearServerRegistry());
	}
}
