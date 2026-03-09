package com.avaricious.upgrades;

import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class DeptRing extends Ring {

    @Override
    public String description() {
        return "Go up to " + Assets.I().yellowText("-20$") + " in dept";
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_12;
    }

    @Override
    public void hit() {
    }
}
