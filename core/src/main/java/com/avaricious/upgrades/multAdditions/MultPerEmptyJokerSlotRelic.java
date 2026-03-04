package com.avaricious.upgrades.multAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.Deck;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

import java.util.List;

public class MultPerEmptyJokerSlotRelic extends MultAdditionRelic {

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getMulti() {
        return (6 - Deck.I().getDeck().size()) * 5;
    }

    @Override
    public String description() {
        return Assets.I().redText("+5 mult") + " for each empty Joker slot\n"
            + Assets.I().greenText("(Currently +" + getMulti() + ")");
    }

    @Override
    public RingAssetKeys ringKey() {
        return RingAssetKeys.RING_25;
    }
}
