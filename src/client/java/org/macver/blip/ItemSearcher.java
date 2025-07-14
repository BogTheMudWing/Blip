package org.macver.blip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;
import org.macver.blip.mixin.client.PlayerInventoryAccessor;

import java.util.*;
import java.util.stream.Stream;

public class ItemSearcher {
    private static final FuzzyScore fuzzyScore = new FuzzyScore(Locale.ENGLISH);

    public List<Item> searchItems(String query) {
        Stream<Item> items;
        // only show all items if in creative
        if (MinecraftClient.getInstance().player.getAbilities().creativeMode) {
            items = Registries.ITEM.stream();
        } else {
            // otherwise only show items from inventory
            PlayerInventoryAccessor inventory = (PlayerInventoryAccessor) MinecraftClient.getInstance().player.getInventory();
            items = inventory.getMain().stream()
                    .filter(stack -> !stack.isEmpty())
                    .map(ItemStack::getItem)
                    .distinct();
        }
        return items
                .map(item -> {
                    String itemName = item.getName().getString();
                    int fuzzy = fuzzyScore.fuzzyScore(itemName, query);
                    int keyword = keywordScore(itemName, query);
                    int finalScore = fuzzy + (10 * keyword); // You can tweak this weight
                    int nameLength = itemName.length();
                    return new ScoredItem(item, finalScore, nameLength);
                })
                .filter(si -> si.score > 0)
                .sorted()
                .map(si -> si.item)
                .toList();
    }

    // Word-based score: counts matching words regardless of order
    private int keywordScore(@NotNull String term, @NotNull String query) {
        Set<String> termWords = new HashSet<>(Arrays.asList(term.toLowerCase().split("\\W+")));
        Set<String> queryWords = new HashSet<>(Arrays.asList(query.toLowerCase().split("\\W+")));

        termWords.retainAll(queryWords); // intersection of words
        return termWords.size();
    }

    private record ScoredItem(Item item, int score, int length) implements Comparable<ScoredItem> {

        @Override
            public int compareTo(@NotNull ScoredItem other) {
                // Higher score first
                if (this.score != other.score) return Integer.compare(other.score, this.score);
                // If scores are equal, prefer shorter names
                return Integer.compare(this.length, other.length);
            }
        }
}