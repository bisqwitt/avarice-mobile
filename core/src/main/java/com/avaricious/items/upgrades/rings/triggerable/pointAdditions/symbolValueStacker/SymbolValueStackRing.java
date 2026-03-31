package com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker;

import com.avaricious.components.slot.Symbol;
import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.items.upgrades.rings.triggerable.AbstractTriggerableRing;
import com.avaricious.items.upgrades.rings.triggerable.ITriggerableOnConditionRing;
import com.avaricious.utility.Assets;

import java.util.List;

public abstract class SymbolValueStackRing extends AbstractTriggerableRing implements ITriggerableOnConditionRing {

    protected int level = 0;
    protected int stacks = 0;

    public abstract Symbol getSymbol();

    @Override
    public void onTrigger() {
        pulse();
        if (onSymbolHit()) {
            echo();
            createNumberPopup(Assets.I().green(), 1);
        }
    }

    @Override
    public boolean condition(List<PatternHitContext> matches, PatternHitContext match) {
        return match.getSymbol() == getSymbol();
    }

    @Override
    public String description() {
        return "Increases value of " + getSymbol().toString()
            + Assets.I().blueText(" (" + getSymbol().baseValue() + ")") + " by 1 every 10 " + getSymbol().toString() + "'s scored\n"
            + Assets.I().greenText("Currently +" + level
            + " (" + stacks + "/" + 10 + ")");
    }

    private boolean onSymbolHit() {
        stacks += 1;
        if (stacks >= 10) {
            stacks = stacks - 10;
            level++;
            getSymbol().setBaseValue(getSymbol().baseValue() + 1);
            return true;
        }
        return false;
    }

    @Override
    public TriggerablePer triggerableOn() {
        return TriggerablePer.SLOT;
    }
}
