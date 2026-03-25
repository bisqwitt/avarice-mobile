package com.avaricious.upgrades.cards;

import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DrawCardsEqualToCurrentStreak extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.OBELISK);

    @Override
    public String description() {
        return "Draw cards equal to current streak\n"
            + "(" + (int) ScoreDisplay.I().getValueOf(ScoreDisplay.Type.STREAK) + ")";
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
    protected void onApply() {
        Hand.I().drawCards((int) ScoreDisplay.I().getValueOf(ScoreDisplay.Type.STREAK));
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> {
        };
    }
}
