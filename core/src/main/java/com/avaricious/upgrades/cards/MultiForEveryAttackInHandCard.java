package com.avaricious.upgrades.cards;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MultiForEveryAttackInHandCard extends AbstractCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.CONSTELLATION_CARD);
    private int multi = 0;

    @Override
    public String description() {
        return Assets.I().redText("+3 Multi") + " for every " + Assets.I().redText("Attack") + " type Card in Hand\n"
            + "(" + attackCardsInHand() + ")";
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
        multi = attackCardsInHand() * 3;
        ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.MULTI, multi);
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(multi, pos, Assets.I().red()));
    }

    private int attackCardsInHand() {
        return (int) Hand.I().getHand().stream()
            .filter(card -> card.type() == CardType.ATTACK)
            .count();
    }
}
