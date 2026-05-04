package com.avaricious.items.upgrades.rings;

import com.avaricious.components.RingBar;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.effects.EffectManager;
import com.avaricious.effects.TextureEcho;
import com.avaricious.items.upgrades.AbstractUpgrade;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.items.upgrades.rings.triggerable.multAdditions.MultiPerEmptyRingSlotRing;
import com.avaricious.items.upgrades.rings.triggerable.multAdditions.RandomMultiAdditionRing;
import com.avaricious.items.upgrades.rings.triggerable.multAdditions.pattern.FiveOfAKindMultiAdditionRing;
import com.avaricious.items.upgrades.rings.triggerable.multAdditions.pattern.FourOfAKindMultiAdditionRing;
import com.avaricious.items.upgrades.rings.triggerable.multAdditions.pattern.ThreeOfAKindMultiAdditionRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.PointsForEveryRingHit;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.PointsPerPatternHit;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.BellValueStackRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CherryValueStackRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CloverValueStackRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.DiamondValueStackRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.IronValueStackRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.LemonValueStackRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.SevenValueStackRing;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRing extends AbstractUpgrade {

    public abstract RingAssetKeys keySet();

    @Override
    public TextureRegion texture() {
        return Assets.I().get(keySet().getTextureKey());
    }

    @Override
    public TextureRegion shadowTexture() {
        return Assets.I().get(keySet().getShadowKey());
    }

    @Override
    public String title() {
        return "Ring";
    }

    @Override
    public IUpgradeType type() {
        return RingType.PASSIVE;
    }

    protected void pulse() {
        body.pulse();

        PointsForEveryRingHit pointsForEveryRingHit = RingBar.I().getRingByClass(PointsForEveryRingHit.class);
        if (pointsForEveryRingHit != null && !(this instanceof PointsForEveryRingHit))
            pointsForEveryRingHit.onRingHit();
    }

    protected void echo() {
        Vector2 renderPos = body.getRenderPos(new Vector2());
        Rectangle bounds = new Rectangle(renderPos.x, renderPos.y, body.getBounds().width, body.getBounds().height);
        TextureEcho.create(Assets.I().get(keySet().getTextureKey()), bounds, TextureEcho.Type.SLOT, EffectManager.streak);
    }

    protected void createNumberPopup(Color popupColor, int value) {
        PopupManager.I().spawnNumber(value, popupColor,
            body.getPos().x + 1.1f, body.getPos().y + 1.1f,
            false);
    }

    @Override
    public int price() {
        return 3;
    }

    @Override
    public float getTextureWidth() {
        return 1.5f;
    }

    @Override
    public float getTextureHeight() {
        return 1.5f;
    }

    @Override
    public float getTooltipYOffset() {
        return 1.7f;
    }

    public static AbstractRing randomRing() {
        return instantiateItem(allRingClasses.get((int) (Math.random() * allRingClasses.size())));
    }

    public static final List<Class<? extends AbstractRing>> allRingClasses = Collections.unmodifiableList(Arrays.asList(
        CriticalDamageRing.class,
        DeptRing.class,
        RandomMultiAdditionRing.class,
        MultiPerEmptyRingSlotRing.class,
        ThreeOfAKindMultiAdditionRing.class,
        FourOfAKindMultiAdditionRing.class,
        FiveOfAKindMultiAdditionRing.class,
        LemonValueStackRing.class,
        CherryValueStackRing.class,
        CloverValueStackRing.class,
        BellValueStackRing.class,
        IronValueStackRing.class,
        DiamondValueStackRing.class,
        SevenValueStackRing.class,
        PointsPerPatternHit.class,
        DoubleXpRing.class,
        OneMoreCardAtStartOfRoundRing.class,
        DoubleSymbolValueDisableFruits.class
    ));
}
