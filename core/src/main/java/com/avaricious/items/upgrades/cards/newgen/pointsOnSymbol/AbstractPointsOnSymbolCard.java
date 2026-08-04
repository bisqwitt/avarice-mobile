package com.avaricious.items.upgrades.cards.newgen.pointsOnSymbol;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.components.slot.Symbol;
import com.avaricious.components.slot.pattern.PatternMatch;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.items.upgrades.cards.CardType;
import com.avaricious.items.upgrades.cards.newgen.ITriggerableCard;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public abstract class AbstractPointsOnSymbolCard extends AbstractCard implements ITriggerableCard {

    abstract Symbol symbol();

    @Override
    public String description() {
        return Assets.I().blueText("+5 Points") + "\nTriggers on Pattern including " + symbol().toString();
    }

    @Override
    protected void onApply() {
        body.pulse();
        ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.POINTS, 5);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(5, pos, Assets.I().blue()));
    }

    @Override
    public IUpgradeType type() {
        return CardType.ATTACK;
    }

    @Override
    public boolean triggerable(List<PatternMatch> matches, PatternMatch patternMatch) {
        int matchIndex = matches.indexOf(patternMatch);
        boolean isLastMatchOfSymbol = matches.size() - 1 == matchIndex || matches.get(matches.indexOf(patternMatch)).getSymbol() != patternMatch.getSymbol();
        return isLastMatchOfSymbol && patternMatch.getSymbol() == symbol();
    }

}
