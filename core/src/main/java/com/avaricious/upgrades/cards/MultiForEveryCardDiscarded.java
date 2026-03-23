package com.avaricious.upgrades.cards;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MultiForEveryCardDiscarded extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.BLACKBOARD_CARD);
    private int mult;

    @Override
    public String description() {
        return Assets.I().redText("+5 Mult") + " for every Card discarded\n("
            + Hand.I().getCardsDiscarded() + ")";
    }

    @Override
    protected void onApply() {
        mult = Hand.I().getCardsDiscarded() * 5;
        ScoreDisplay.I().addTo(ScoreDisplay.Type.MULTI, mult);
    }

    @Override
    public IUpgradeType type() {
        return CardType.ATTACK;
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(mult, pos, Assets.I().red()));
    }
}
