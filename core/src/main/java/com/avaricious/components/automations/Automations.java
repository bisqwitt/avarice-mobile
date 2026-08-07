package com.avaricious.components.automations;

public class Automations {

    private static Automations instance;

    public static Automations I() {
        return instance == null ? instance = new Automations() : instance;
    }

    private final AutoSpinAutomation autoSpin = new AutoSpinAutomation();
    private final AutoSpinCapacity autoSpinCapacity = new AutoSpinCapacity();

    private final HandCapacity handCapacity = new HandCapacity();

    private final SpinBuyerAutomation spinBuyer = new SpinBuyerAutomation();

    public AutoSpinAutomation getAutoSpin() {
        return autoSpin;
    }

    public AutoSpinCapacity getAutoSpinCapacity() {
        return autoSpinCapacity;
    }

    public SpinBuyerAutomation getSpinBuyer() {
        return spinBuyer;
    }

    public HandCapacity getHandCapacity() {
        return handCapacity;
    }
}
