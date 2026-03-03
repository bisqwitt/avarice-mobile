package com.avaricious.upgrades;

import com.avaricious.utility.Assets;
import com.avaricious.utility.RingKey;

public class CriticalHitDamageRelic extends Relic {
    @Override
    public String description() {
        return "Double " + Assets.I().greenText("Critical Hit") + " Multiplier";
    }

    @Override
    public RingKey ringKey() {
        return RingKey.RING_4;
    }
}
