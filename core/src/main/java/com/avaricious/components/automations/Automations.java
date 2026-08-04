package com.avaricious.components.automations;

public class Automations {

    private static Automations instance;

    public static Automations I() {
        return instance == null ? instance = new Automations() : instance;
    }

    private final AutoSpinAutomation autoSpin = new AutoSpinAutomation();

    public AutoSpinAutomation getAutoSpin() {
        return autoSpin;
    }
}
