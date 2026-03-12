package com.avaricious.upgrades.cards;

import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TwoPointsForEverySymbolHit extends Card {

    private final TextureRegion texture = Assets.I().get(AssetKey.SPACE_JOKER_CARD);
    private int points = 0;

    @Override
    public String description() {
        return Assets.I().blueText("+2 Points") + " for every symbol hit last spin\n"
            + "(" + ScreenManager.I().getScreen(SlotScreen.class).getSymbolsHitLastSpin() + ")";
    }

    @Override
    protected void onApply() {
        points = ScreenManager.I().getScreen(SlotScreen.class).getSymbolsHitLastSpin() * 2;
        ScoreDisplay.I().addTo(ScoreDisplay.Type.POINTS, points);
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
