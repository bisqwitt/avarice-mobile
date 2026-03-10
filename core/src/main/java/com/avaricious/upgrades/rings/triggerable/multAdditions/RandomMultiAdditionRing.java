package com.avaricious.upgrades.rings.triggerable.multAdditions;

import com.avaricious.upgrades.rings.triggerable.ITriggerablePerSpinRing;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;
import com.badlogic.gdx.math.MathUtils;

public class RandomMultiAdditionRing extends AbstractMultiAdditionRing implements ITriggerablePerSpinRing {

    @Override
    public int getMulti() {
        return MathUtils.random(0, 23);
    }

    @Override
    public String description() {
        return Assets.I().redText("+0") + " - " + Assets.I().redText("23 Mult") + "\n"
            + "(Random each turn)";
    }

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_44;
    }
}
