package com.avaricious.upgrades.cards;

import com.avaricious.components.HealthUi;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ConvertPointsToArmorCard extends Card {

    private final TextureRegion texture = Assets.I().get(AssetKey.BALL8_CARD);
    private int points;

    @Override
    public String description() {
        return "Converts " + Assets.I().blueText("Points") + " ("
            + Assets.I().blueText(((int) ScoreDisplay.I().getValueOf(ScoreDisplay.Type.POINTS)) + "") + ")\nto "
            + Assets.I().silverText("Armor");
    }

    @Override
    protected void onApply() {
        points = (int) ScoreDisplay.I().getValueOf(ScoreDisplay.Type.POINTS);
        ScoreDisplay.I().setValueOf(ScoreDisplay.Type.POINTS, 0);
        HealthUi.I().addArmor(points);
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> {
            PopupManager.I().spawnNumber(createNumberPopup(points, pos, Assets.I().silver()));
            pos.y -= 1f;
            PopupManager.I().spawnNumber(createNumberPopup(-points, pos, Assets.I().blue()));
        };
    }
}
