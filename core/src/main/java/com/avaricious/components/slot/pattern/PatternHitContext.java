package com.avaricious.components.slot.pattern;

import com.avaricious.components.slot.Body;

import java.util.List;

public class PatternHitContext {

    private final PatternMatch match;
    private final List<Body> bodies;

    public PatternHitContext(PatternMatch match, List<Body> slots) {
        this.match = match;
        this.bodies = slots;
    }

    public PatternMatch getMatch() {
        return match;
    }

    public List<Body> getSlots() {
        return bodies;
    }
}
