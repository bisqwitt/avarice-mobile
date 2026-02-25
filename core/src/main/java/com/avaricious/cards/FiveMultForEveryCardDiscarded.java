package com.avaricious.cards;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.upgrades.Hand;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Vector2;

public class FiveMultForEveryCardDiscarded extends Card {

    private int mult;

    @Override
    public String description() {
        return Assets.I().redText("+5 Mult") + " for every Card discarded\n("
            + Hand.I().getCardsDiscarded() + ")";
    }

    @Override
    protected void onApply() {
        mult = Hand.I().getCardsDiscarded() * 5;
        PatternDisplay.I().addTo(PatternDisplay.Type.MULTI, mult);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(mult, pos, Assets.I().red()));
    }
}
