package com.thexyro.screenmod.client;

import java.nio.file.Path;
import java.util.function.Consumer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

/** Isletim sisteminin "Dosya sec" penceresini acar (ayri thread'de, oyun donmasin diye). */
public final class FilePicker {
	private FilePicker() { }

	public static void open(Consumer<Path> callback) {
		Thread thread = new Thread(() -> {
			String result;
			try (MemoryStack stack = MemoryStack.stackPush()) {
				PointerBuffer filters = stack.mallocPointer(3);
				filters.put(stack.UTF8("*.png"));
				filters.put(stack.UTF8("*.jpg"));
				filters.put(stack.UTF8("*.jpeg"));
				filters.flip();
				result = TinyFileDialogs.tinyfd_openFileDialog("Select an image", "", filters, "Images (png, jpg)", false);
			}
			if (result != null) {
				callback.accept(Path.of(result));
			}
		}, "screenmod-file-dialog");
		thread.setDaemon(true);
		thread.start();
	}
}
