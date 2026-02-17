package com.avaricious.cards;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.utility.Assets;
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
        return () -> PopupManager.I().spawnNumber(createNumberPopup(2, pos, Assets.I().red()));
    }
}
