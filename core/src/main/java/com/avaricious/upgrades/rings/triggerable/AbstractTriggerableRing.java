package com.avaricious.upgrades.rings.triggerable;

import com.avaricious.TaskScheduler;
import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.upgrades.rings.AbstractRing;
import com.avaricious.upgrades.rings.RingType;

import java.util.List;

public abstract class AbstractTriggerableRing extends AbstractRing {

    public abstract TriggerablePer triggerableOn();

    protected abstract void onTrigger();

    @Override
    public IUpgradeType type() {
        return RingType.ACTIVE;
    }

    public void scheduleTrigger(List<PatternHitContext> matches, PatternHitContext match, boolean scheduleWithoutDelay) {
        if (!(this instanceof ITriggerableOnConditionRing)
            || ((ITriggerableOnConditionRing) this).condition(matches, match)) {

            if(scheduleWithoutDelay) TaskScheduler.I().scheduleNoDelay(this::onTrigger);
            else TaskScheduler.I().schedule(this::onTrigger);
        }
    }

    public enum TriggerablePer {
        SLOT,
        PATTERN,
        SPIN
    }

}
