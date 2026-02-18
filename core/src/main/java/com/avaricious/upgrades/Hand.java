package com.avaricious.upgrades;

import com.avaricious.cards.Card;
import com.avaricious.utility.Observable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand extends Observable<List<? extends Card>> {

    private static Hand instance;

    public static Hand I() {
        return instance == null ? instance = new Hand() : instance;
    }

    private Hand() {
        addCardFromDeck();
        addCardFromDeck();
        addCardFromDeck();
    }

    private final List<Card> hand = new ArrayList<>();

    @Override
    protected List<? extends Card> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(hand));
    }

    public void addCardFromDeck() {
        Card upgrade = Deck.I().pickCardFromDeck();
        if (upgrade == null) return;

        addCardToHand(upgrade);
    }

    public <T> List<T> getUpgradesOfClass(Class<T> clazz) {
        List<T> out = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {          // deck is a List<?>
            Object o = hand.get(i);
            if (clazz.isInstance(o)) {
                out.add(clazz.cast(o));
            }
        }
        return out;
    }

    public <T> T getUpgradeOfClass(Class<T> upgradeClass) {
        for (Card upgrade : hand) {
            if (upgradeClass.isInstance(upgrade)) return (T) upgrade;
        }
        return null;
    }

    private void addCardToHand(Card upgrade) {
        hand.add(upgrade);
        notifyChanged(snapshot());
    }

    public void removeCardFromHand(Card upgrade) {
        hand.remove(upgrade);
        notifyChanged(snapshot());
    }

    public int cardsHeldInHand() {
        return hand.size();
    }

    public List<Card> getHand() {
        return hand;
    }
}
