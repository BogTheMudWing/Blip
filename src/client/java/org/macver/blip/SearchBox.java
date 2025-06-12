package org.macver.blip;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

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
                boolean countSpecified = false;
                ItemStack stack = new ItemStack(results.getFirst()); // Pick top result
                try {
                    int count = getCount(input);
                    stack.setCount(count);
                    countSpecified = true;
                } catch (Exception ignored) {
                }
                // If 0 items are requested, do nothing
                if (stack.getCount() == 0) {
                    client.setScreen(null);
                    return true;
                }

                ClientPlayerEntity player = client.player;

                PlayerInventory inventory = player.getInventory();
                int selectedSlot = inventory.selectedSlot;

                if (!countSpecified) {
                    // If count is not specified, check if the player already has the itemstack
                    int slotWithStack = -1;
                    for (int i = 0; i < inventory.size(); i++) {
                        // A for loop is used instead of inventory.getSlotWithStack(stack) so that count is irrelevant
                        if (inventory.getStack(i).isOf(stack.getItem())) {
                            slotWithStack = i;
                            break;
                        }
                    }
                    if (slotWithStack != -1) {
                        // If the stack exists, switch to it
                        switchHandToItemStack(slotWithStack, inventory, selectedSlot);
                    } else {
                        // Player does not have itemstack
                        if (player.isCreative()) giveStack(inventory, selectedSlot, stack);
                        else {
                            player.sendMessage(Text.translatable("message.survial_not_enough_items").getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst(), true);
                        }
                    }
                } else {
                    if (player.isCreative()) giveStack(inventory, selectedSlot, stack);
                    else {

                        player.sendMessage(Text.translatable("message.not_supported").getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst(), true);

//                        int slotWithStack = -1;
//
//                        // Find a stack with matching item regardless of count
//                        for (int i = 0; i < inventory.size(); i++) {
//                            // A for loop is used instead of inventory.getSlotWithStack(stack) so that count is irrelevant
//                            if (inventory.getStack(i).isOf(stack.getItem())) {
//                                slotWithStack = i;
//                                break;
//                            }
//                        }
//
//                        ItemStack existingStack = inventory.getStack(slotWithStack);
//
//                        if (slotWithStack == -1 || existingStack.getCount() < stack.getCount()) {
//                            player.sendMessage(Text.translatable("message.survial_not_enough_items").getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst(), true);
//                        } else if (existingStack.getCount() == stack.getCount()) {
//                            // If counts match exactly do a basic swap
//                            swapSlots(slotWithStack, selectedSlot);
//                        } else {
//                            // Pick up items
//                            existingStack.setCount(existingStack.getCount() - stack.getCount());
//                            inventory.setStack(selectedSlot, stack);
//                            // This shows how this should work, but changes are not sent to the server, so it doesn't actually work.
//                        }
                    }
                }
            }

            client.setScreen(null); // Close the UI
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void giveStack(@NotNull PlayerInventory inventory, int selectedSlot, ItemStack stack) {
        if (inventory.getStack(selectedSlot).isEmpty()) {
            // If hand is empty, place stack there
            setStackToSlot(stack, selectedSlot);
        } else {
            // If slot is not empty, find next empty hotbar slot
            int newSlot = findEmptyHotbarSlot(selectedSlot, inventory);

            if (newSlot != -1) {
                // If a hotbar slot was found, place stack there
                inventory.setSelectedSlot(newSlot);
                setStackToSlot(stack, newSlot);
            } else {
                // If there are no empty hotbar slots, see if there is an empty slot in the inventory
                int emptySlot = inventory.getEmptySlot();
                if (emptySlot != -1) {
                    // If one is found, move current hand item into there
                    swapSlots(emptySlot, selectedSlot);
                }

                // Replace hand
                setStackToSlot(stack, selectedSlot);
            }

        }
    }

    private void swapSlots(int slot1, int slot2) {
        client.interactionManager.clickSlot(
                client.player.playerScreenHandler.syncId,
                slot1,
                slot2,
                SlotActionType.SWAP,
                client.player
        );
    }

    private static int findEmptyHotbarSlot(int selectedSlot, PlayerInventory inventory) {
        int newSlot = -1;
        for (int i = selectedSlot + 1; i < 9; i++) {
            if (inventory.getStack(i).isEmpty()) {
                newSlot = i;
                break;
            }
        }
        if (newSlot == -1) {
            for (int i = 0; i < selectedSlot; i++) {
                if (inventory.getStack(i).isEmpty()) {
                    newSlot = i;
                    break;
                }
            }
        }
        return newSlot;
    }

    private void setStackToSlot(ItemStack stack, int selectedSlot) {

        if (selectedSlot == 0) {
            // Workaround for slot 0 because it doesn't work normally for some reason
            selectedSlot = 40;

            client.interactionManager.clickCreativeStack(stack, selectedSlot);
            swapSlots(selectedSlot, 0);
            client.interactionManager.clickCreativeStack(new ItemStack(Registries.ITEM.get(Identifier.of("air"))), selectedSlot);
            swapSlots(selectedSlot, selectedSlot);
            return;
        }

        client.interactionManager.clickCreativeStack(stack, selectedSlot);
        swapSlots(selectedSlot, selectedSlot);
    }

    private void switchHandToItemStack(int slotWithStack, PlayerInventory inventory, int selectedSlot) {
        // Player already has the itemstack
        if (slotWithStack < 9) {
            // Stack is in hotbar, switch to it
            inventory.setSelectedSlot(slotWithStack);
        } else {
            // Stack is in inventory, swap to it
            swapSlots(slotWithStack, selectedSlot);
        }
    }

    public List<Item> searchItems(@NotNull String query) {
        if (!query.isEmpty() && Character.isDigit(query.charAt(query.length() - 1))) {
            // Number at the end, include count
            String[] split = query.split(" ");
            String s = split[split.length - 1];
            try {
                Integer.parseInt(s);
                int endIndex = query.length() - s.length();
                String substring = query.substring(0, endIndex);
                return itemSearcher.searchItems(substring.strip().toLowerCase(Locale.ROOT));
            } catch (NumberFormatException ignored) {}
        } else if (!query.isEmpty() && Character.isDigit(query.charAt(0))) {
            // Number at the end, include count
            String[] split = query.split(" ");
            String s = split[0];
            try {
                Integer.parseInt(s);
                int startIndex = s.length();
                String substring = query.substring(startIndex);
                return itemSearcher.searchItems(substring.strip().toLowerCase(Locale.ROOT));
            } catch (NumberFormatException ignored) {}
        }
        return itemSearcher.searchItems(query.strip().toLowerCase(Locale.ROOT));
    }

    /**
     * Get the count from the query
     * @param input the {@link String} representing the input query
     * @return the count, if it exists.
     * @throws NoSuchElementException if there is no count.
     */
    private int getCount(@NotNull String input) {
        if (Character.isDigit(input.charAt(input.length() - 1))) {
            // Number at the end, include count
            String[] split = input.split(" ");
            String s = split[split.length - 1];
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {}
        } else if (Character.isDigit(input.charAt(0))) {
            // Number at the end, include count
            String[] split = input.split(" ");
            String s = split[0];
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {}
        }
        throw new NoSuchElementException();
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

            try {
                // Include count if it exists
                int count = getCount(input);
                context.drawText(textRenderer, count + " " + item.getName().getString(), x + 20, y + i * 20, 0xFFFFFF, true);
            } catch (NoSuchElementException ignored) {
                // No number at the end, draw normally
                context.drawText(textRenderer, item.getName(), x + 20, y + i * 20, 0xFFFFFF, true);
            }

        }
    }
}