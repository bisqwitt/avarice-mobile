package com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.rings.triggerable.AbstractTriggerableRing;
import com.avaricious.upgrades.rings.triggerable.ITriggerablePerSlotRing;
import com.avaricious.utility.Assets;

public abstract class SymbolValueStackRing extends AbstractTriggerableRing implements ITriggerablePerSlotRing {

    protected int level = 0;
    protected int stacks = 0;

    @Override
    public String description() {
        return "Increases value of " + getSymbol().toString()
            + Assets.I().blueText(" (" + getSymbol().baseValue() + ")") + " by 1 every 10 " + getSymbol().toString() + "'s scored\n"
            + Assets.I().greenText("Currently +" + level
            + " (" + stacks + "/" + 10 + ")");
    }

    public boolean addStacks(int amount) {
        stacks += amount;
        if (stacks >= 10) {
            stacks = stacks - 10;
            level++;
            addPointsToSymbolBaseValue();
            return true;
        }
        return false;
    }

    private void addPointsToSymbolBaseValue() {
        getSymbol().setBaseValue(getSymbol().baseValue() + 1);
    }

    public abstract Symbol getSymbol();

    @Override
    public void onTrigger() {
        pulse();
        createNumberPopup(Assets.I().green(), 1);
    }

    public void enoughStacks() {
        pulse();
        createNumberPopup(Assets.I().blue(), 1);
    }
}
