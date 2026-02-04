package com.avaricious.upgrades.pointAdditions;

import com.avaricious.Assets;
import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.UpgradeRarity;
import com.avaricious.upgrades.UpgradeWithActionAfterSpin;

import java.util.List;

public class PointsPerConsecutiveHit extends PointAdditionUpgrade implements UpgradeWithActionAfterSpin {

    private int hitNumber = 1;

    public PointsPerConsecutiveHit(UpgradeRarity rarity) {
        super(rarity);
    }

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
