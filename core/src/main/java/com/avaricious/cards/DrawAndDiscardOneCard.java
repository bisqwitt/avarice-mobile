package com.avaricious.cards;

import com.avaricious.upgrades.Deck;
import com.avaricious.upgrades.Hand;
import com.badlogic.gdx.math.Vector2;

public class DrawAndDiscardOneCard extends Card {
    @Override
    public String description() {
        return "Draw and Discard one Card";
    }

    @Override
    protected void onApply() {
        Hand hand = Hand.I();
        hand.removeCardFromHand(hand.getRandomCard());
        hand.addCardFromDeck();
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return null;
    }
}
