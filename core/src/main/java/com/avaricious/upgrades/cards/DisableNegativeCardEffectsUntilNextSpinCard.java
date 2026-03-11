package com.avaricious.upgrades.cards;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DisableNegativeCardEffectsUntilNextSpinCard extends Card {
    @Override
    public String description() {
        return "";
    }

    @Override
    protected void onApply() {

    }

    @Override
    public TextureRegion texture() {
        return null;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return null;
    }
}
