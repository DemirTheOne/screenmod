package com.thexyro.screenmod.client;

import com.thexyro.screenmod.ScreenBlock;
import com.thexyro.screenmod.ScreenMod;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ScreenModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ScreenMod.SCREEN_BLOCK_ENTITY, ScreenBlockEntityRenderer::new);
		ScreenBlock.OPEN_SCREEN = pos -> Minecraft.getInstance().setScreen(new ScreenMenuScreen(pos));
	}
}
