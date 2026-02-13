package com.avaricious;

import com.avaricious.upgrades.DeptUpgrade;
import com.avaricious.upgrades.Deck;
import com.avaricious.utility.Observable;

public class CreditManager extends Observable<Integer> {

    private static CreditManager instance;

    public static CreditManager I() {
        return instance == null ? (instance = new CreditManager()) : instance;
    }

    private CreditManager() {
        credits = 5;
    }

    private int credits;

    public void gain(int amount) {
        setCredits(credits + amount);
    }

    public void pay(int amount) {
        setCredits(credits - amount);
    }

    public void roundEnd() {
        gain((int) (3 + Math.min((double) (credits / 5), 5)));
    }

    public int getCredits() {
        return credits;
    }

    private void setCredits(int newValue) {
        credits = newValue;
        notifyChanged(newValue);
    }

    public boolean enoughCredit(int value) {
        int base = Deck.I().upgradeIsInDeck(DeptUpgrade.class) ? -20 : 0;
        return credits - value >= base;
    }

    @Override
    protected Integer snapshot() {
        return credits;
    }
}
