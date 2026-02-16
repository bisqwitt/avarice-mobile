package com.avaricious.upgrades.pointAdditions;

import com.avaricious.components.slot.Symbol;

import java.util.List;

public class PointsPerStreak extends PointAdditionRelic {

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return false;
    }

    @Override
    public int getPoints() {
        return 0;
    }

    @Override
    public String description() {
        return "";
    }
}
