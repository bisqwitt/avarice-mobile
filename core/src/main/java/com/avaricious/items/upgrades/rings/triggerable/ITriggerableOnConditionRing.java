package com.avaricious.items.upgrades.rings.triggerable;

import com.avaricious.components.slot.pattern.PatternMatch;

import java.util.List;

public interface ITriggerableOnConditionRing {

    boolean condition(List<PatternMatch> matches, PatternMatch match);

}
