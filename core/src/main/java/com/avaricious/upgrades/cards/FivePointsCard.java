package com.avaricious.upgrades.cards;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class FivePointsCard extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.CEREMONIAL_DAGGER_CARD);

    @Override
    public String description() {
        return Assets.I().blueText("+5 Points");
    }

    @Override
    public void onApply() {
        ScoreDisplay.I().addTo(ScoreDisplay.Type.POINTS, 5);
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(5, pos, Assets.I().blue()));
    }
}
