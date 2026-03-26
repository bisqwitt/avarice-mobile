package com.avaricious.upgrades.cards;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class PointsForEachCardInHandCard extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.DELAYED_GRATIFICATION_CARD);
    private int points = 0;

    @Override
    public String description() {
        return Assets.I().blueText("+5 Points") + " for every Card held in Hand"
            + "\n(" + Hand.I().cardsHeldInHand() + ")";
    }

    @Override
    protected void onApply() {
        points = Hand.I().cardsHeldInHand() * 5;
        ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.POINTS, points);
    }

    @Override
    public IUpgradeType type() {
        return CardType.ATTACK;
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(points, pos, Assets.I().blue()));
    }
}
