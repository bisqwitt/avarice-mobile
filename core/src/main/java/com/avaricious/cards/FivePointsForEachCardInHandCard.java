package com.avaricious.cards;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.upgrades.Hand;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Vector2;

public class FivePointsForEachCardInHandCard extends Card {

    private int points = 0;

    @Override
    public String description() {
        return Assets.I().blueText("+5 Points") + "for every Card held in Hand"
            + "\n(" + Hand.I().cardsHeldInHand() + ")";
    }

    @Override
    protected void onApply() {
        points = Hand.I().cardsHeldInHand() * 5;
        PatternDisplay.I().addPoints(points);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(points, pos, Assets.I().blue()));
    }
}
