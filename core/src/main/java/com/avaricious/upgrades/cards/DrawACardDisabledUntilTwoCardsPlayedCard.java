package com.avaricious.upgrades.cards;

import com.avaricious.RoundsManager;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DrawACardDisabledUntilTwoCardsPlayedCard extends AbstractCard implements IConditionalApplyCard {

    private final TextureRegion texture = Assets.I().get(AssetKey.BUSINESS_CARD);

    @Override
    public String description() {
        return "Draw a card, is disabled until two cards played\n"
            + "(" + cardsPlayedThisRound() + ")";
    }

    @Override
    public IUpgradeType type() {
        return CardType.UTILITY;
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    protected void onApply() {
        Hand.I().drawCard();
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> {
        };
    }

    private int cardsPlayedThisRound() {
        return RoundsManager.I().getPlayedCardsThisRound().size();
    }

    @Override
    public boolean condition() {
        return cardsPlayedThisRound() >= 2;
    }
}
