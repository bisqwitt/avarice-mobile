package com.avaricious.upgrades.rings.triggerable.pointAdditions;

import com.avaricious.upgrades.rings.triggerable.ITriggerablePerSpinRing;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class PointsForEveryRingHit extends AbstractPointAdditionRing implements ITriggerablePerSpinRing {

    private int pointAddition = 5;
    private int ringHits = 0;

    @Override
    public int getPoints() {
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
//        createNumberPopup(Assets.I().green(), 1);
    }

    @Override
    public String description() {
        return Assets.I().blueText("+" + pointAddition + " Points") + ". Permanently increases by " + Assets.I().blueText("2") + " for every 10 Ring hits\n"
            + "(Currently " + ringHits + " / 10)";
    }
}
