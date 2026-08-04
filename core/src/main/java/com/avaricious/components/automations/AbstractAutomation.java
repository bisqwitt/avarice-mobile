package com.avaricious.components.automations;

public abstract class AbstractAutomation {

    private boolean active;

    public void activate() {
        active = true;
        onActivate();
    }

    protected abstract void onActivate();

    public boolean isActive() {
        return active;
    }

}
