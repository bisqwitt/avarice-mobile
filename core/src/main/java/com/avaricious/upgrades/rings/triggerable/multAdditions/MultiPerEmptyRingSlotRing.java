package com.avaricious.upgrades.rings.triggerable.multAdditions;

import com.avaricious.components.RingBar;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class MultiPerEmptyRingSlotRing extends AbstractMultiAdditionRing {

    @Override
    public int getValue() {
        return (5 - RingBar.I().size()) * 5;
    }

    @Override
    public String description() {
        return Assets.I().redText("+5 Multi") + " for each empty Ring slot\n"
            + Assets.I().greenText("(Currently +" + getValue() + ")");
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_25;
    }

    @Override
    public TriggerablePer triggerableOn() {
        return TriggerablePer.SPIN;
    }
}
