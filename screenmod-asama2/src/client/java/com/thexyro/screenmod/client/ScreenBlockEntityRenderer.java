package com.thexyro.screenmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.thexyro.screenmod.ScreenBlock;
import com.thexyro.screenmod.ScreenBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

/** Resmi bloğun on yuzunde cizer. */
public class ScreenBlockEntityRenderer implements BlockEntityRenderer<ScreenBlockEntity, ScreenBlockEntityRenderer.ScreenRenderState> {
	private static final int FULL_BRIGHT = 15728880;

	public ScreenBlockEntityRenderer(BlockEntityRendererProvider.Context context) { }

	public static class ScreenRenderState extends BlockEntityRenderState {
		public Identifier texture;
		public float width = 1.0F;
		public float height = 1.0F;
		public float yaw;
	}

	@Override
	public ScreenRenderState createRenderState() {
		return new ScreenRenderState();
	}

	@Override
	public void extractRenderState(ScreenBlockEntity blockEntity, ScreenRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
		state.texture = null;

		String image = blockEntity.getImage();
		if (image == null) {
			return;
		}
		ScreenImageCache.Entry entry = ScreenImageCache.get(image);
		if (entry == null) {
			return;
		}
		state.texture = entry.id();

		float w = 1.0F;
		float h = 1.0F;
		if (blockEntity.isKeepAspect()) {
			float ratio = (float) entry.width() / (float) entry.height();
			if (ratio > 1.0F) {
				h = 1.0F / ratio;
			} else {
				w = ratio;
			}
		}
		state.width = w;
		state.height = h;

		Direction facing = blockEntity.getBlockState().getValue(ScreenBlock.FACING);
		state.yaw = 180.0F - facing.toYRot();
	}

	@Override
	public void submit(ScreenRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.texture == null) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.5, 0.5, 0.5);
		poseStack.mulPose(Axis.YP.rotationDegrees(state.yaw));
		poseStack.translate(-0.5, -0.5, -0.5);

		final float x0 = 0.5F - state.width / 2.0F;
		final float x1 = 0.5F + state.width / 2.0F;
		final float y0 = 0.5F - state.height / 2.0F;
		final float y1 = 0.5F + state.height / 2.0F;
		final float z = -0.002F; // bloğun on yuzunun hemen onu

		collector.submitCustomGeometry(poseStack, RenderTypes.text(state.texture), (pose, consumer) -> {
			consumer.addVertex(pose, x1, y1, z).setColor(0xFFFFFFFF).setUv(0.0F, 0.0F).setLight(FULL_BRIGHT);
			consumer.addVertex(pose, x0, y1, z).setColor(0xFFFFFFFF).setUv(1.0F, 0.0F).setLight(FULL_BRIGHT);
			consumer.addVertex(pose, x0, y0, z).setColor(0xFFFFFFFF).setUv(1.0F, 1.0F).setLight(FULL_BRIGHT);
			consumer.addVertex(pose, x1, y0, z).setColor(0xFFFFFFFF).setUv(0.0F, 1.0F).setLight(FULL_BRIGHT);
		});

		poseStack.popPose();
	}
}
