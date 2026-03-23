package com.avaricious.bosses;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CherryValueStackRing;

public class CherryDebuffBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Cherries are debuff";
    }

    @Override
    public Upgrade loot() {
        return new CherryValueStackRing();
    }
}
