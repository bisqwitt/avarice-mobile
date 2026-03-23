package com.avaricious.bosses;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.cards.MultiForEveryCardDiscarded;

public class DiscardACardAfterEveryPlayedCardBoss extends AbstractBoss {
    @Override
    public String description() {
        return "Discard a card after every played card";
    }

    @Override
    public Upgrade loot() {
        return new MultiForEveryCardDiscarded();
    }
}
