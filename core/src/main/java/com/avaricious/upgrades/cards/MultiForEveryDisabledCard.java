package com.avaricious.upgrades.cards;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MultiForEveryDisabledCard extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.FANCE);
    private int multi = 0;

    @Override
    public String description() {
        return Assets.I().redText("+7 Multi") + " for every disabled Card in Hand\n"
            + "(" + disabledCardsInHand() + ")";
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
    protected void onApply() {
        multi = disabledCardsInHand() * 7;
        ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.MULTI, multi);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(multi, pos, Assets.I().red()));
    }

    private int disabledCardsInHand() {
        return (int) Hand.I().getHand().stream()
            .filter(AbstractCard::isDisabled)
            .count();
    }
}
