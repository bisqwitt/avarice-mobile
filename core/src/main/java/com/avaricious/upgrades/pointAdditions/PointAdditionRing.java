package com.avaricious.upgrades.pointAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.Ring;

import java.util.List;

public abstract class PointAdditionRing extends Ring {

    public abstract boolean condition(List<Symbol> selection, long count);

    public abstract int getPoints();

}
