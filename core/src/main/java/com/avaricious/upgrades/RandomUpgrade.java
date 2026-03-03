package com.avaricious.upgrades;

import com.avaricious.utility.RingKey;

public class RandomUpgrade extends Relic {
    @Override
    public String description() {
        return "Random Relic";
    }

    @Override
    public RingKey ringKey() {
        return null;
    }
}
