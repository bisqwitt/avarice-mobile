package com.avaricious.upgrades.pointAdditions;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.RelicWithActionAfterSpin;
import com.avaricious.utility.Assets;

import java.util.List;

public class PointsPerConsecutiveHit extends PointAdditionRelic implements RelicWithActionAfterSpin {

    private int hitNumber = 1;

    @Override
    public boolean condition(List<Symbol> selection, long count) {
        return true;
    }

    @Override
    public int getPoints() {
        hitNumber++;
        return hitNumber - 1;
    }

    @Override
    public String description() {
        return Assets.I().blueText("+1") + " per consecutive hit\n"
            + Assets.I().greenText("(Currently +" + hitNumber + ")");
    }

    @Override
    public void onSpinEnded() {
        hitNumber = 1;
    }
}
