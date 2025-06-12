package org.macver.blip;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SearchBox extends Screen {
    public SearchBox(Text title) {
        super(title);
    }
    TextFieldWidget textFieldWidget;
    final ItemSearcher itemSearcher = new ItemSearcher();

    // Called at the beginning
    @Override
    protected void init() {

        int boxWidth = 200;
        int boxHeight = 20;

        int x = (this.width - boxWidth) / 2;
        int y = (this.height - boxHeight) / 2 - 40;

        textFieldWidget = new TextFieldWidget(textRenderer, x, y, boxWidth, boxHeight, Text.of("Search"));
        textFieldWidget.setChangedListener(this::searchItems);

        // Register the button widget.
        this.addDrawableChild(textFieldWidget);
        this.setInitialFocus(textFieldWidget);

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            String input = textFieldWidget.getText();
            List<Item> results = searchItems(input);
            if (!results.isEmpty()) {
                ItemStack stack = new ItemStack(results.getFirst()); // Pick top result
                client.player.setStackInHand(Hand.MAIN_HAND, stack);

            }
            client.setScreen(null); // Close the UI
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public List<Item> searchItems(@NotNull String query) {
        return itemSearcher.searchItems(query.toLowerCase(Locale.ROOT));
    }


    // Called every frame
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String input = textFieldWidget.getText();
        List<Item> suggestions = searchItems(input).stream().limit(5).toList();

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow
//        context.drawText(textRenderer, "Special Button", 40, 40 - textRenderer.fontHeight - 10, 0xFFFFFFFF, true);

        int boxWidth = 200;
        int boxHeight = 20;

        int x = (this.width - boxWidth) / 2;
        int y = (this.height - boxHeight) / 2 - 10;

        for (int i = 0; i < suggestions.size(); i++) {
            Item item = suggestions.get(i);
            context.drawItem(item.getDefaultStack(), x, y - 5 + i * 20);
            context.drawText(textRenderer, item.getName(), x + 20, y + i * 20, 0xFFFFFF, true);
        }
    }
}