package com.avaricious.upgrades.multAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;

import java.util.List;
import java.util.Map;

public class DefaultMultAdditionUpgrade extends MultAdditionUpgrade {

    public DefaultMultAdditionUpgrade(UpgradeRarity rarity) {
        super(rarity);
    }

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
