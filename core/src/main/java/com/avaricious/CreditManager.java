package com.avaricious;

import com.avaricious.upgrades.DeptUpgrade;
import com.avaricious.upgrades.UpgradesManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntConsumer;

public class CreditManager {

    private static CreditManager instance;

    public static CreditManager I() {
        return instance == null ? (instance = new CreditManager()) : instance;
    }

    private CreditManager() {
        credits = 5;
    }

    private int credits;

    private final List<IntConsumer> listeners = new CopyOnWriteArrayList<>();

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
        notifyCreditChanged(newValue);
    }

    public AutoCloseable onCreditChange(IntConsumer listener) {
        listeners.add(listener);

        listener.accept(credits);
        return () -> listeners.remove(listener);
    }

    public boolean enoughCredit(int value) {
        int base = UpgradesManager.I().getUpgradesOfClass(DeptUpgrade.class).findAny().isPresent() ? -20 : 0;
        return credits - value >= base;
    }

    private void notifyCreditChanged(int newValue) {
        listeners.forEach(listener -> listener.accept(newValue));
    }
}
