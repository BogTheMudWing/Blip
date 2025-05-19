package org.macver.blip;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class BlipClient implements ClientModInitializer {

	private static KeyBinding keyBinding;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.blip.open", // The translation key
				InputUtil.Type.KEYSYM, // Type of keybinding is keyboard
				GLFW.GLFW_KEY_C, // The keycode
				"category.blip.test"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding.wasPressed()) {
				MinecraftClient.getInstance().setScreen(
						new BlipBox(Text.empty())
				);
			}
		});
	}
}