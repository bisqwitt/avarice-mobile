package com.avaricious.upgrades.pointAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.Relic;

import java.util.List;

public abstract class PointAdditionRelic extends Relic {

    public abstract boolean condition(List<Symbol> selection, long count);

    public abstract int getPoints();

}
