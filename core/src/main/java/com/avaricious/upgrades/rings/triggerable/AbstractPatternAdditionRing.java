package com.avaricious.upgrades.rings.triggerable;

import com.avaricious.audio.AudioManager;
import com.avaricious.components.ScreenShake;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.effects.EffectManager;
import com.avaricious.utility.Assets;

public abstract class AbstractPatternAdditionRing extends AbstractTriggerableRing {

    protected abstract PatternDisplay.Type getPatternType();

    public abstract int getValue();

    @Override
    protected void onTrigger() {
        pulse();
        echo();
        addToPattern(getPatternType(), getValue());
        ScreenShake.I().addTrauma(0.2f);
        AudioManager.I().playHit(EffectManager.streak);
    }

    private void addToPattern(PatternDisplay.Type type, int value) {
        PatternDisplay.I().addTo(type, value);
        createNumberPopup(type == PatternDisplay.Type.POINTS ? Assets.I().blue() : Assets.I().red(), value);
    }
}
