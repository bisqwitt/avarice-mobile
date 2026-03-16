package com.avaricious.upgrades.rings.triggerable;

import com.avaricious.audio.AudioManager;
import com.avaricious.components.ScreenShake;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.effects.EffectManager;
import com.avaricious.utility.Assets;

public abstract class AbstractScoreAdditionRing extends AbstractTriggerableRing {

    protected abstract ScoreDisplay.Type getPatternType();

    public abstract int getValue();

    @Override
    protected void onTrigger() {
        pulse();
        echo();
        addToPattern(getPatternType(), getValue());
        ScreenShake.I().addTrauma(0.2f);
        AudioManager.I().playHit(EffectManager.streak);
    }

    private void addToPattern(ScoreDisplay.Type type, int value) {
        ScoreDisplay.I().addTo(type, value);
        createNumberPopup(type == ScoreDisplay.Type.POINTS ? Assets.I().blue() : Assets.I().red(), value);
    }
}
