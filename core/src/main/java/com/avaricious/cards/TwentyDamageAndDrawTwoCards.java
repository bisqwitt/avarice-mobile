package com.avaricious.cards;

import com.avaricious.components.HealthUi;
import com.avaricious.upgrades.Hand;
import com.badlogic.gdx.math.Vector2;

public class TwentyDamageAndDrawTwoCards extends Card {
    @Override
    public String description() {
        return "Draw two Cards, deals 20 damage";
    }

    @Override
    protected void onApply() {
        HealthUi.I().damage(20);
        Hand hand = Hand.I();
        hand.queueActions(
            hand::drawCard,
            hand::drawCard
        );
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> {
        };
    }
}
