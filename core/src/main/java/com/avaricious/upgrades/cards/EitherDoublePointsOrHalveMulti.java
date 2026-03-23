package com.avaricious.upgrades.cards;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EitherDoublePointsOrHalveMulti extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.ABSTRACT_JOKER_CARD);

    private boolean doublePoints;
    private int value;

    @Override
    public String description() {
        return "1/2 Chance, either Double " + Assets.I().blueText("Points") + " or Halve " + Assets.I().redText("Multi");
    }

    @Override
    public IUpgradeType type() {
        return CardType.ATTACK;
    }

    @Override
    protected void onApply() {
        doublePoints = MathUtils.random(0, 1) == 0;
        value = doublePoints
            ? (int) ScoreDisplay.I().getValueOf(ScoreDisplay.Type.POINTS)
            : (int) -ScoreDisplay.I().getValueOf(ScoreDisplay.Type.MULTI) / 2;
        ScoreDisplay.I().addTo(doublePoints
                ? ScoreDisplay.Type.POINTS
                : ScoreDisplay.Type.MULTI,
            value);
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(value, pos, doublePoints ? Assets.I().blue() : Assets.I().red()));
    }
}
