package com.avaricious.upgrades.cards;

import com.avaricious.components.displays.ScoreDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.components.slot.Symbol;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class OnePointForEveryFruitCard extends Card {

    private final TextureRegion texture = Assets.I().get(AssetKey.DNA_CARD);
    int points = 0;

    @Override
    public String description() {
        return Assets.I().blueText("+1 Point") + " for every fruit displayed\n"
            + "(" + countFruits() + ")";
    }

    @Override
    protected void onApply() {
        points = countFruits();
        ScoreDisplay.I().addTo(ScoreDisplay.Type.POINTS, points);
    }

    @Override
    public TextureRegion texture() {
        return texture;
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
                if (symbol.isFruit()) fruitCount++;
            }
        }
        return fruitCount;
    }
}
