package com.avaricious.upgrades.rings;

import com.avaricious.components.RingBar;
import com.avaricious.utility.RingAssetKeys;

import java.util.List;

public class TriggerRingToTheRightTwiceRing extends AbstractRing {
    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_43;
    }

    @Override
    public String description() {
        return "Trigger the ring to the right twice\n"
            + "(" + (ringToTheRight() == null ? "null" : ringToTheRight().title()) + ")";
    }

    private AbstractRing ringToTheRight() {
        List<AbstractRing> rings = RingBar.I().getRings();
        int ringIndex = rings.indexOf(this);
        return rings.size() >= ringIndex ? rings.get(ringIndex + 1) : null;
    }
}
