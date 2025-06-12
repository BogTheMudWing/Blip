package org.macver.blip;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.apache.commons.text.similarity.FuzzyScore;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemSearcher {
    private static final FuzzyScore fuzzyScore = new FuzzyScore(Locale.ENGLISH);

    public List<Item> searchItems(String query) {
        return Registries.ITEM.stream()
                .map(item -> {
                    String itemName = item.getName().getString();
                    int score = fuzzyScore.fuzzyScore(itemName, query);
                    int nameLength = itemName.length();
                    return new ScoredItem(item, score, nameLength);
                })
                .filter(si -> si.score > 0)
                .sorted()
                .map(si -> si.item)
                .toList();
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