package com.avaricious.upgrades.cards;

import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DrawAndDiscardOneCard extends AbstractCard {

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
    public IUpgradeType type() {
        return CardType.UTILITY;
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
