package com.avaricious.upgrades.pointAdditions.symbolValueStacker;

import com.avaricious.Assets;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.pointAdditions.PointAdditionRelic;

import java.util.List;

public abstract class SymbolValueStackRelic extends PointAdditionRelic {

    protected int level = 0;
    protected int stacks = 0;

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getPoints() {
        return 0;
    }

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
}
