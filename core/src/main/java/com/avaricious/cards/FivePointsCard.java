package com.avaricious.cards;

import com.avaricious.Assets;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.NumberPopup;
import com.avaricious.components.popups.PopupManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FivePointsCard extends Card {

    @Override
    public String description() {
        return Assets.I().blueText("+5 Points");
    }

    @Override
    public void onApply() {
        PatternDisplay.I().addPoints(5);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(5, Assets.I().blue(), new Rectangle(pos.x, pos.y, NumberPopup.defaultWidth * 1.3f, NumberPopup.defaultHeight * 1.3f), false);
    }
}
