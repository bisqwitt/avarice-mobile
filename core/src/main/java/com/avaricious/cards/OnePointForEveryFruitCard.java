package com.avaricious.cards;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.math.Vector2;

public class OnePointForEveryFruitCard extends Card {

    int points = 0;

    @Override
    public String description() {
        return Assets.I().blueText("+1 Point") + " for every fruit\n"
            + "(" + countFruits() + ")";
    }

    @Override
    protected void onApply() {
        points = countFruits();
        PatternDisplay.I().addPoints(points);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(points, pos, Assets.I().blue()));
    }

    private int countFruits() {
        int fruitCount = 0;
        Symbol[][] symbolMap = SlotMachine.I().getSymbolMap();
        for (Symbol[] row : symbolMap) {
            for (Symbol symbol : row) {
                if (symbol == Symbol.LEMON || symbol == Symbol.CHERRY) fruitCount++;
            }
        }
        return fruitCount;
    }
}
