package com.avaricious.cards;

import com.avaricious.upgrades.Upgrade;
import com.badlogic.gdx.math.Vector2;

public abstract class Card extends Upgrade {

    public abstract String description();

    protected abstract void onApply();

    public abstract Runnable createPopupRunnable(Vector2 pos);

    public void apply() {
        onApply();
    }

}
