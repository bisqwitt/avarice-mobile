package com.avaricious.bosses;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.cards.HealForEveryFruitHitCard;

public class DoubleDamageBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Double Damage";
    }

    @Override
    public Upgrade loot() {
        return new HealForEveryFruitHitCard();
    }
}
