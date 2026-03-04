package com.avaricious.cards;

import com.avaricious.upgrades.Hand;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DrawAndDiscardOneCard extends Card {

    private final TextureRegion texture = Assets.I().get(AssetKey.BANNER_CARD);

    @Override
    public String description() {
        return "Draw and Discard one Card";
    }

    @Override
    protected void onApply() {
        Hand hand = Hand.I();
        hand.queueActions(
            hand::discardRandomCard,
            hand::drawCard
        );
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> {
        };
    }
}
