package com.avaricious.cards;

import com.avaricious.Assets;
import com.avaricious.components.popups.NumberPopup;
import com.avaricious.components.popups.PopupManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TenArmorCard extends Card {
    @Override
    public String description() {
        return "Gain 10 Armor";
    }

    @Override
    protected void onApply() {

    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(10, Assets.I().silver(), new Rectangle(pos.x, pos.y, NumberPopup.defaultWidth * 1.3f, NumberPopup.defaultHeight * 1.3f), false);
    }
}
