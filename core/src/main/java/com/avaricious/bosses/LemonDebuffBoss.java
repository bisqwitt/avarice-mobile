package com.avaricious.bosses;

import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.LemonValueStackRing;

public class LemonDebuffBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Lemons are debuffed";
    }

    @Override
    public AbstractUpgrade loot() {
        return new LemonValueStackRing();
    }
}
