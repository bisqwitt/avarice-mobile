package com.avaricious.upgrades.multAdditions;

import com.avaricious.components.slot.Symbol;

import java.util.List;

public class DefaultMultAdditionRelic extends MultAdditionRelic {

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getMulti() {
        return 2;
    }

    @Override
    public String description() {
        return "Add " + getMulti() + " to multiplier";
    }
}
