package com.avaricious.upgrades;

import com.avaricious.Assets;

public class CriticalHitDamageRelic extends Relic {
    @Override
    public String description() {
        return "Double " + Assets.I().greenText("Critical Hit") + " Multiplier";
    }
}
