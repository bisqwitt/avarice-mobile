package com.avaricious.components.slot;

import com.avaricious.utility.AssetKey;

public enum Symbol {

    LEMON(2, 19.4f, AssetKey.LEMON, true),
    CHERRY(2, 19.4f, AssetKey.CHERRY, true),
    CLOVER(3, 14.9f, AssetKey.CLOVER, false),
    BELL(3, 14.9f, AssetKey.BELL, false),
    IRON(5, 11.9f, AssetKey.IRON, false),
    DIAMOND(5, 11.9f, AssetKey.DIAMOND, false),
    SEVEN(7, 7.5f, AssetKey.SEVEN, false);

    private Integer baseValue;
    private final Float baseSpawnChance;
    private final AssetKey assetKey;

    private final boolean isFruit;

    Symbol(Integer baseValue, Float baseSpawnChance, AssetKey assetKey, boolean isFruit) {
        this.baseValue = baseValue;
        this.baseSpawnChance = baseSpawnChance;
        this.assetKey = assetKey;
        this.isFruit = isFruit;
    }

    public Integer baseValue() {
        return baseValue;
    }

    public void setBaseValue(int value) {
        baseValue = value;
    }

    public Float baseSpawnChance() {
        return baseSpawnChance;
    }

    public AssetKey assetKey() {
        return assetKey;
    }

    public String toString() {
        return this.name().replace(".png", "");
    }

    public boolean isFruit() {
        return isFruit;
    }
}
