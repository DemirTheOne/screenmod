package com.thexyro.screenmod.client;

import com.thexyro.screenmod.ScreenBlockEntity;
import com.thexyro.screenmod.net.AspectPayload;
import com.thexyro.screenmod.net.UploadImagePayload;
import java.nio.file.Path;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

/**
 * Ekrana sag tiklayinca acilan menu (Gallery / Settings / Import).
 * ASAMA 2: bilgisayardan resim yukleme, resmi kaldirma ve en-boy orani calisiyor.
 */
public class ScreenMenuScreen extends Screen {
	private enum Tab { GALLERY, SETTINGS, IMPORT }

	private final BlockPos pos;
	private Tab tab = Tab.GALLERY;
	private boolean keepAspect = true;
	private volatile String status = "";

	public ScreenMenuScreen(BlockPos pos) {
		super(Component.literal("Screen"));
		this.pos = pos;
		ScreenBlockEntity be = getBlockEntity();
		if (be != null) {
			this.keepAspect = be.isKeepAspect();
		}
	}

	private ScreenBlockEntity getBlockEntity() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null && mc.level.getBlockEntity(this.pos) instanceof ScreenBlockEntity be) {
			return be;
		}
		return null;
	}

	private boolean hasImage() {
		ScreenBlockEntity be = getBlockEntity();
		return be != null && be.getImage() != null;
	}

	@Override
	protected void init() {
		int w = Math.min(320, this.width - 20);
		int left = (this.width - w) / 2;
		int third = w / 3;

		addTabButton(left, third, Tab.GALLERY, "Gallery");
		addTabButton(left + third, third, Tab.SETTINGS, "Settings");
		addTabButton(left + third * 2, third, Tab.IMPORT, "Import");

		switch (tab) {
			case GALLERY -> {
				this.addRenderableWidget(Button.builder(Component.literal("Remove image"), b -> {
					ClientPlayNetworking.send(new UploadImagePayload(this.pos, new byte[0]));
					status = "Image removed";
				}).bounds(left, 90, w, 20).build());
			}
			case SETTINGS -> {
				EditBox idBox = new EditBox(this.font, left, 90, w - 70, 20, Component.literal("Screen ID"));
				idBox.setHint(Component.literal("Screen ID..."));
				this.addRenderableWidget(idBox);
				Button link = Button.builder(Component.literal("Link"), b -> { })
						.bounds(left + w - 65, 90, 65, 20).build();
				link.active = false;
				this.addRenderableWidget(link);
				this.addRenderableWidget(Button.builder(aspectLabel(), b -> {
					keepAspect = !keepAspect;
					b.setMessage(aspectLabel());
					ClientPlayNetworking.send(new AspectPayload(this.pos, keepAspect));
				}).bounds(left, 120, w, 20).build());
			}
			case IMPORT -> {
				this.addRenderableWidget(Button.builder(Component.literal("Upload from Computer"), b -> {
					status = "Choose an image in the window that opened...";
					FilePicker.open(this::handleFile);
				}).bounds(left, 70, w, 20).build());
			}
		}

		this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
				.bounds(left, this.height - 30, w, 20).build());
	}

	/** Dosya secilince (ayri thread'de) calisir: kucult, sonra sunucuya yolla. */
	private void handleFile(Path path) {
		try {
			status = "Processing image...";
			byte[] jpeg = ImageUtil.prepareJpeg(path);
			Minecraft.getInstance().execute(() -> {
				ClientPlayNetworking.send(new UploadImagePayload(this.pos, jpeg));
				status = "Uploaded!";
			});
		} catch (Exception e) {
			status = "Error: " + e.getMessage();
		}
	}

	private void addTabButton(int x, int width, Tab target, String label) {
		String text = (tab == target ? "> " : "") + label;
		this.addRenderableWidget(Button.builder(Component.literal(text), b -> {
			tab = target;
			this.rebuildWidgets();
		}).bounds(x, 40, width, 20).build());
	}

	private Component aspectLabel() {
		return Component.literal("Maintain Aspect Ratio: " + (keepAspect ? "ON" : "OFF"));
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		super.render(g, mouseX, mouseY, partialTick);
		g.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
		String shown = hasImage() ? "Image currently displayed" : "No image currently displayed";
		g.drawCenteredString(this.font, Component.literal(shown), this.width / 2, 25, 0xFFAAAAAA);
		int left = (this.width - Math.min(320, this.width - 20)) / 2;
		switch (tab) {
			case GALLERY -> { }
			case SETTINGS -> g.drawString(this.font, Component.literal("Screen ID:"), left, 78, 0xFFFFFFFF);
			case IMPORT -> g.drawCenteredString(this.font, Component.literal("PNG or JPG"), this.width / 2, 100, 0xFFAAAAAA);
		}
		if (!status.isEmpty()) {
			g.drawCenteredString(this.font, Component.literal(status), this.width / 2, this.height - 45, 0xFFFFFF55);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
