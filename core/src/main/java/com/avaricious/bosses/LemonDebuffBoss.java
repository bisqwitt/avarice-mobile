package com.avaricious.bosses;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.LemonValueStackRing;

public class LemonDebuffBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Lemon's are debuffed";
    }

    @Override
    public Upgrade loot() {
        return new LemonValueStackRing();
    }
}
