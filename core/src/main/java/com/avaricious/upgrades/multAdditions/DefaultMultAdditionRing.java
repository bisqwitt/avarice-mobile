package com.avaricious.upgrades.multAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.RingAssetKeys;

import java.util.List;

public class DefaultMultAdditionRing extends MultAdditionRing {

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getMulti() {
        return 2;
    }

    @Override
    public String description() {
        return "Add " + getMulti() + " to multiplier";
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_19;
    }
}
