package com.avaricious.items.upgrades.rings.triggerable;

import com.avaricious.components.slot.pattern.PatternHitContext;

import java.util.List;

public interface ITriggerableOnConditionRing {

    boolean condition(List<PatternHitContext> matches, PatternHitContext match);

}
