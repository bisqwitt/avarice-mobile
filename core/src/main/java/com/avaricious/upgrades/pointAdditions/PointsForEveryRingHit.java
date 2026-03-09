package com.avaricious.upgrades.pointAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

import java.util.List;

public class PointsForEveryRingHit extends PointAdditionRing {

    private int pointAddition = 5;
    private int ringHits = 0;

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getPoints() {
        return pointAddition;
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_80;
    }

    @Override
    public void hit() {
        ringHits++;
        if (ringHits == 10) {
            ringHits = 0;
            pointAddition += 2;
        }

        pulse();
        numberPopup(Assets.I().green(), 1);
    }

    @Override
    public String description() {
        return Assets.I().blueText("+" + pointAddition + " Points") + ". Permanently increases by " + Assets.I().blueText("2") + " for every 10 Ring hits\n"
            + "(Currently " + ringHits + " / 10)";
    }
}
