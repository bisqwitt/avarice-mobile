package com.avaricious.upgrades;

public abstract class Upgrade {

    private UpgradeRarity rarity;

    public Upgrade(UpgradeRarity rarity) {
        this.rarity = rarity;
    }

    public abstract String description();

    public UpgradeRarity getRarity() {
        return rarity;
    }
}
