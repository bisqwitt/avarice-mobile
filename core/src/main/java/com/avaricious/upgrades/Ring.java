package com.avaricious.upgrades;

import com.avaricious.utility.RingAssetKeys;

public abstract class Ring extends Upgrade {

    public abstract RingAssetKeys keySet();

    @Override
    public String title() {
        return "Ring";
    }
}
