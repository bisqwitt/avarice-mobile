package com.avaricious.upgrades.multAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.Relic;

import java.util.List;

public abstract class MultAdditionRelic extends Relic {

    public abstract boolean condition(List<Symbol> selection, long count);

    public abstract int getMulti();

}
