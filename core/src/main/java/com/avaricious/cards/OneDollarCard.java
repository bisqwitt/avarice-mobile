package com.avaricious.cards;

import com.avaricious.Assets;
import com.avaricious.CreditManager;
import com.avaricious.components.popups.NumberPopup;
import com.avaricious.components.popups.PopupManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class OneDollarCard extends Card {
    @Override
    public String description() {
        return Assets.I().yellowText("+1$");
    }

    @Override
    protected void onApply() {
        CreditManager.I().gain(1);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnCreditNumber(1, new Rectangle(pos.x, pos.y, NumberPopup.defaultWidth * 1.3f, NumberPopup.defaultHeight * 1.3f), false);
    }
}
