package com.avaricious.bosses;

import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CherryValueStackRing;

public class CherryDebuffBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Cherries are debuffed";
    }

    @Override
    public AbstractUpgrade loot() {
        return new CherryValueStackRing();
    }
}
