package com.avaricious.upgrades;

import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;

public class DeptRelic extends Relic {

    @Override
    public String description() {
        return "Go up to " + Assets.I().yellowText("-20$") + " in dept";
    }

    @Override
    public RingAssetKeys ringKey() {
        return RingAssetKeys.RING_12;
    }
}
