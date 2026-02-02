package com.avaricious.components.slot;

import com.avaricious.AssetKey;

public enum Symbol {

    LEMON(2, 19.4f, AssetKey.LEMON),
    CHERRY(2, 19.4f, AssetKey.CHERRY),
    CLOVER(3, 14.9f, AssetKey.CLOVER),
    BELL(3, 14.9f, AssetKey.BELL),
    IRON(5, 11.9f, AssetKey.IRON),
    DIAMOND(5, 11.9f, AssetKey.DIAMOND),
    SEVEN(7, 7.5f, AssetKey.SEVEN);

    private Integer baseValue;
    private final Float baseSpawnChance;
    private final AssetKey assetKey;

    Symbol(Integer baseValue, Float baseSpawnChance, AssetKey assetKey) {
        this.baseValue = baseValue;
        this.baseSpawnChance = baseSpawnChance;
        this.assetKey = assetKey;
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
}
