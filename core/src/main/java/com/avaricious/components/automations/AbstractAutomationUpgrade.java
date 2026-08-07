package com.avaricious.components.automations;

public abstract class AbstractAutomationUpgrade extends AbstractAutomation {

    @Override
    protected void onActivate() {
    }

    public void upgrade() {
        onUpgrade();
    }

    abstract void onUpgrade();

    abstract boolean isMaxed();

    @Override
    public boolean isBuyable() {
        return !isMaxed() && isActive();
    }
}
