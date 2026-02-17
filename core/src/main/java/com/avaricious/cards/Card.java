package com.avaricious.cards;

import com.avaricious.components.popups.NumberPopup;
import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Card extends Upgrade {

    public abstract String description();

    protected abstract void onApply();

    public abstract Runnable createPopupRunnable(Vector2 pos);

    protected NumberPopup createNumberPopup(int value, Vector2 pos, Color color) {
        return new NumberPopup(value, color, posToBounds(pos), false, false);
    }

    protected Rectangle posToBounds(Vector2 pos) {
        return new Rectangle(pos.x, pos.y, NumberPopup.defaultWidth * 1.3f, NumberPopup.defaultHeight * 1.3f);
    }

    public void apply() {
        onApply();
    }

}
