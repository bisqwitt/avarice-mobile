package com.avaricious.upgrades;

public class CriticalHitDamageUpgrade extends Upgrade {
    public CriticalHitDamageUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

    @Override
    public String description() {
        return "Double " + Assets.I().greenText("Critical Hit") + " Multiplier";
    }

    @Override
    public void apply() {

    }
}
