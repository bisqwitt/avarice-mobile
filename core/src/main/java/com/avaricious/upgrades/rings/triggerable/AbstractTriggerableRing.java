package com.avaricious.upgrades.rings.triggerable;

import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.upgrades.rings.AbstractRing;

import java.util.List;

public abstract class AbstractTriggerableRing extends AbstractRing {

    protected abstract void onTrigger();

    public void trigger(List<PatternHitContext> matches, PatternHitContext match) {
        if (!(this instanceof ITriggerableOnConditionRing)
            || ((ITriggerableOnConditionRing) this).condition(matches, match)) {
            this.onTrigger();
        }
    }

}
