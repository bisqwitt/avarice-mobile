package com.avaricious.components.slot;

import com.avaricious.RoundsManager;
import com.avaricious.bosses.DecreaseSymbolValueBoss;
import com.avaricious.utility.AssetKey;

public enum Symbol {

    LEMON(2, 24, AssetKey.LEMON, AssetKey.LEMON_SHADOW, AssetKey.LEMON_WHITE, true),
    CHERRY(2, 24, AssetKey.CHERRY, AssetKey.CHERRY_SHADOW, AssetKey.CHERRY_WHITE, true),
    CLOVER(3, 16, AssetKey.CLOVER, AssetKey.CLOVER_SHADOW, AssetKey.CLOVER_WHITE, false),
    BELL(3, 16, AssetKey.BELL, AssetKey.BELL_SHADOW, AssetKey.BELL_WHITE, false),
    IRON(5, 8, AssetKey.IRON, AssetKey.IRON_SHADOW, AssetKey.IRON_WHITE, false),
    DIAMOND(5, 8, AssetKey.DIAMOND, AssetKey.DIAMOND_SHADOW, AssetKey.DIAMOND_WHITE, false),
    SEVEN(7, 4, AssetKey.SEVEN, AssetKey.SEVEN_SHADOW, AssetKey.SEVEN_WHITE, false);

    private Integer baseValue;
    private Integer baseSpawnChance;
    private final AssetKey texture;
    private final AssetKey shadowTexture;
    private final AssetKey whiteTexture;

    private final boolean isFruit;

    Symbol(Integer baseValue, Integer baseSpawnChance, AssetKey texture, AssetKey shadowTexture, AssetKey whiteTexture, boolean isFruit) {
        this.baseValue = baseValue;
        this.baseSpawnChance = baseSpawnChance;
        this.texture = texture;
        this.shadowTexture = shadowTexture;
        this.whiteTexture = whiteTexture;
        this.isFruit = isFruit;
    }

    public Integer baseValue() {
        return RoundsManager.I().getBoss() instanceof DecreaseSymbolValueBoss
            ? baseValue - 1
            : baseValue;
    }

    public void setBaseValue(int value) {
        baseValue = value;
    }

    public Integer baseSpawnChance() {
        return baseSpawnChance;
    }

    public void setBaseSpawnChance(int value) {
        baseSpawnChance = value;
    }

    public AssetKey textureKey() {
        return texture;
    }

    public AssetKey shadowKey() {
        return shadowTexture;
    }

    public AssetKey whiteKey() {
        return whiteTexture;
    }

    public String toString() {
        return this.name().replace(".png", "");
    }

    public boolean isFruit() {
        return isFruit;
    }
}
