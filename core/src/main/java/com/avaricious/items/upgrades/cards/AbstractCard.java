package com.avaricious.items.upgrades.cards;

import com.avaricious.components.HealthUi;
import com.avaricious.components.popups.NumberPopup;
import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.GameStateLogger;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCard extends AbstractUpgrade {

    public static float WIDTH = 142;
    public static float HEIGHT = 190;

    public abstract String description();

    protected abstract void onApply();

    public abstract Runnable createPopupRunnable(Vector2 pos);

    protected NumberPopup createNumberPopup(int value, Vector2 pos, Color color) {
        return new NumberPopup(value, color, posToBounds(pos), false, false);
    }

    protected Rectangle posToBounds(Vector2 pos) {
        return new Rectangle(pos.x, pos.y, NumberPopup.defaultWidth * 1.3f, NumberPopup.defaultHeight * 1.3f);
    }

    public void apply() {
        onApply();
    }

    @Override
    public TextureRegion shadowTexture() {
        return Assets.I().get(AssetKey.JOKER_CARD_SHADOW);
    }

    @Override
    public String title() {
        return "Card";
    }

    @Override
    public int price() {
        return 3;
    }

    public boolean isDisabled() {
        if (this instanceof IConditionalApplyCard && !((IConditionalApplyCard) this).condition())
            return true;
        if (GameStateLogger.I().defenceTypeCardsDisabled() && this.type() == CardType.DEFENCE)
            return true;
        if (GameStateLogger.I().cardsDisabledOnNoArmor() && HealthUi.I().getArmor() == 0)
            return true;

        return false;
    }

    @Override
    public float getTextureHeight() {
        return HEIGHT;
    }

    @Override
    public float getTextureWidth() {
        return WIDTH;
    }

    @Override
    public float getTooltipYOffset() {
        return 2.6f;
    }

    public static AbstractCard randomCard() {
        return instantiateItem(allCardClasses.get((int) (Math.random() * allCardClasses.size())));
    }

    public static final List<Class<? extends AbstractCard>> allCardClasses = Collections.unmodifiableList(Arrays.asList(
        PointsCard.class,
        MultiCard.class,
        ArmorCard.class,
        OneDollarCard.class,
        ConvertPointsToArmorCard.class,
        PointsForEachCardInHandCard.class,
        DrawACardIfLastCard.class,
        MultiForEveryCardDiscarded.class,
        DrawTwoCardsForTenDamage.class,
        PointsForEverySymbolHit.class,
        PointsForEveryFruitCard.class,
        EitherDoublePointsOrHalveMulti.class,
        HealForEveryFruitHitCard.class,
        MultiForEveryAttackInHandCard.class,
        DrawACardDefenceCardsDisabledCard.class,
        MultiForEveryDisabledCard.class,
        DrawACardDisabledUntilTwoCardsPlayedCard.class,
        DrawCardsEqualToCurrentStreak.class,
        DrawAndDiscardACard.class,
        DrawTwoCardsDisabledOnZeroDefence.class,
        MultiplyCurrentArmorByTwoCard.class,
        ArmorForEverySymbolHitLastSpin.class
    ));
}
