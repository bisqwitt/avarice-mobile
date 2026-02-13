package com.avaricious.upgrades;

import com.avaricious.utility.Observable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand extends Observable<List<? extends Upgrade>> {

    private static Hand instance;

    public static Hand I() {
        return instance == null ? instance = new Hand() : instance;
    }

    private Hand() {}

    private final List<Upgrade> hand = new ArrayList<>();

    @Override
    protected List<? extends Upgrade> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(hand));
    }

    public void addCardFromDeck() {
        Upgrade upgrade = Deck.I().pickUpgradefromDeck();
        if(upgrade == null) return;

        addCardToHand(upgrade);
    }

    private void addCardToHand(Upgrade upgrade) {
        hand.add(upgrade);
        notifyChanged(snapshot());
    }
}
