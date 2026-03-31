package com.avaricious.bosses;

import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.cards.MultiForEveryCardDiscarded;

public class DiscardACardAfterEveryPlayedCardBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Discard a card after every played card";
    }

    @Override
    public AbstractUpgrade loot() {
        return new MultiForEveryCardDiscarded();
    }
}
