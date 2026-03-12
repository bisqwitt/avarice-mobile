package com.avaricious.upgrades.rings.triggerable.multAdditions;

import com.avaricious.audio.AudioManager;
import com.avaricious.components.ScreenShake;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.effects.EffectManager;
import com.avaricious.upgrades.rings.triggerable.AbstractPatternAdditionRing;
import com.avaricious.upgrades.rings.triggerable.AbstractTriggerableRing;

public abstract class AbstractMultiAdditionRing extends AbstractPatternAdditionRing {
    @Override
    protected PatternDisplay.Type getPatternType() {
        return PatternDisplay.Type.MULTI;
    }
}
