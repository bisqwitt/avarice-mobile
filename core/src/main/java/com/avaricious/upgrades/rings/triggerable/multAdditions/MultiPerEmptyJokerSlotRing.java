package com.avaricious.upgrades.rings.triggerable.multAdditions;

import com.avaricious.upgrades.Deck;
import com.avaricious.upgrades.rings.triggerable.ITriggerablePerSpinRing;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class MultiPerEmptyJokerSlotRing extends AbstractMultiAdditionRing implements ITriggerablePerSpinRing {

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
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_25;
    }
}
