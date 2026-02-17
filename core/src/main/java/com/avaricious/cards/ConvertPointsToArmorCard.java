package com.avaricious.cards;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Vector2;

public class ConvertPointsToArmorCard extends Card {

    private int points;

    @Override
    public String description() {
        return "Converts " + Assets.I().blueText("Points") + " ("
            + Assets.I().blueText(PatternDisplay.I().getPoints() + "") + ")\nto "
            + Assets.I().silverText("Armor");
    }

    @Override
    protected void onApply() {
        points = (int) PatternDisplay.I().getPoints();
        PatternDisplay.I().setPoints(0);
        ScreenManager.I().getScreen(SlotScreen.class).getArmorBar().heal(points);
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
