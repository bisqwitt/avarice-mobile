package com.avaricious.cards;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Vector2;

public class FivePointsCard extends Card {

    @Override
    public String description() {
        return Assets.I().blueText("+5 Points");
    }

    @Override
    public void onApply() {
        PatternDisplay.I().addTo(PatternDisplay.Type.POINTS, 5);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(5, pos, Assets.I().blue()));
    }
}
