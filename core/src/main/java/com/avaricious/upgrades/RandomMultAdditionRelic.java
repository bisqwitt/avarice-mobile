package com.avaricious.upgrades;

import com.avaricious.Assets;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.multAdditions.MultAdditionRelic;
import com.badlogic.gdx.math.MathUtils;

import java.util.List;

public class RandomMultAdditionRelic extends MultAdditionRelic {

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getMulti() {
        return MathUtils.random(0, 23);
    }

    @Override
    public String description() {
        return Assets.I().redText("+0") + " - " + Assets.I().redText("23 Mult") + "\n"
            + "(Random each turn)";
    }
}
