package com.avaricious.upgrades;

public enum Rarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY;

    public Rarity next() {
        Rarity[] values = Rarity.values();
        int nextIndex = this.ordinal() + 1;

        if (nextIndex >= values.length) {
            return this; // already the highest rarity
        }

        return values[nextIndex];
    }
}
