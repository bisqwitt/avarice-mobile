package com.avaricious.items.upgrades.rings.triggerable.pointAdditions;

import com.avaricious.items.upgrades.IUpgradeWithActionOnSpinButtonPressed;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class PointsPerPatternHit extends AbstractPointAdditionRing implements IUpgradeWithActionOnSpinButtonPressed {

    private int hitNumber = 0;

    @Override
    public int getValue() {
        return hitNumber;
    }

    @Override
    public String description() {
        return Assets.I().blueText("+1") + " per consecutive hit\n"
            + Assets.I().greenText("(Currently +" + hitNumber + ")");
    }

    @Override
    public void onSpinButtonPressed() {
        hitNumber = 0;
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_32;
    }

    public void onPatternHit() {
        hitNumber++;
    }

    @Override
    public TriggerablePer triggerableOn() {
        return TriggerablePer.SLOT;
    }
}
