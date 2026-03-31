package com.avaricious.bosses;

import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.cards.HealForEveryFruitHitCard;

public class DoubleDamageBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Double Damage";
    }

    @Override
    public AbstractUpgrade loot() {
        return new HealForEveryFruitHitCard();
    }
}
