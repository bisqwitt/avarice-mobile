package com.avaricious.items.upgrades.rings.triggerable.pointAdditions;

import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class PointsForEveryRingHit extends AbstractPointAdditionRing {

    private int pointAddition = 5;
    private int ringHits = 0;

    @Override
    public int getValue() {
        return pointAddition;
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_80;
    }

    public void onRingHit() {
        ringHits++;
        if (ringHits == 10) {
            ringHits = 0;
            pointAddition += 2;
        }

        pulse();
    }

    @Override
    public String description() {
        return Assets.I().blueText("+" + pointAddition + " Points") + ". Permanently increases by " + Assets.I().blueText("2") + " for every 10 Ring hits\n"
            + Assets.I().greenText("(Currently " + ringHits + " / 10)");
    }

    @Override
    public TriggerablePer triggerableOn() {
        return TriggerablePer.SPIN;
    }
}
