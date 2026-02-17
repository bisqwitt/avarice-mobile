package com.avaricious.cards;

import com.avaricious.CreditManager;
import com.avaricious.components.popups.CreditNumberPopup;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.utility.Assets;
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
        return () -> PopupManager.I().spawnNumber(new CreditNumberPopup(1,
            posToBounds(pos), false, false));
    }
}
