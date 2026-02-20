package com.avaricious.cards;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Vector2;

public class TwoPointsForEverySymbolHit extends Card {

    private int points = 0;

    @Override
    public String description() {
        return Assets.I().blueText("+2 Points") + " for every symbol hit last spin\n"
            + "(" + ScreenManager.I().getScreen(SlotScreen.class).getSymbolsHitLastSpin() + ")";
    }

    @Override
    protected void onApply() {
        points = ScreenManager.I().getScreen(SlotScreen.class).getSymbolsHitLastSpin() * 2;
        PatternDisplay.I().addPoints(points);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(points, pos, Assets.I().blue()));
    }
}
