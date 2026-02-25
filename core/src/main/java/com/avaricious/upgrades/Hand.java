package com.avaricious.upgrades;

import com.avaricious.cards.Card;
import com.avaricious.utility.Observable;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Hand extends Observable<List<? extends Card>> {

    private static Hand instance;

    public static Hand I() {
        return instance == null ? instance = new Hand() : instance;
    }

    private Hand() {
    }

    private final List<Card> hand = new ArrayList<>();
    private int startingHandSize = 3;

    private int cardsDiscarded = 0;

    @Override
    protected List<? extends Card> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(hand));
    }

    public void drawCard() {
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
        Deck.I().addUpgradeToDeck(upgrade);
        notifyChanged(snapshot());
    }

    public void discardCard(Card card) {
        cardsDiscarded++;
        removeCardFromHand(card);
    }

    public void discardRandomCard() {
        cardsDiscarded++;
        removeCardFromHand(getRandomCard());
    }

    public void queueActions(Runnable... actions) {
        for (int i = 0; i < actions.length; i++) {
            final int index = i;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    actions[index].run();
                }
            }, i * 0.25f);
        }
    }

    public int cardsHeldInHand() {
        return hand.size();
    }

    public List<Card> getHand() {
        return hand;
    }

    public Card getRandomCard() {
        return hand.get(new Random().nextInt(hand.size()));
    }

    public void setStartingHandSize(int startingHandSize) {
        this.startingHandSize = startingHandSize;
    }

    public int getCardsDiscarded() {
        return cardsDiscarded;
    }

    public int getStartingHandSize() {
        return startingHandSize;
    }
}
