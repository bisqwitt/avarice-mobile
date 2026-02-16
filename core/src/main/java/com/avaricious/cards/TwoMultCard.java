package com.avaricious.cards;

import com.avaricious.Assets;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.NumberPopup;
import com.avaricious.components.popups.PopupManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TwoMultCard extends Card {

    @Override
    public String description() {
        return Assets.I().redText("+2 Multi");
    }

    @Override
    public void onApply() {
        PatternDisplay.I().addMulti(2);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(2, Assets.I().red(), new Rectangle(pos.x, pos.y, NumberPopup.defaultWidth * 1.3f, NumberPopup.defaultHeight * 1.3f), false);
    }
}
