package com.avaricious.upgrades.rings;

import com.avaricious.components.RingBar;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.effects.EffectManager;
import com.avaricious.effects.TextureEcho;
import com.avaricious.upgrades.IUpgradeType;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.rings.triggerable.multAdditions.MultiPerEmptyRingSlotRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.RandomMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.pattern.FiveOfAKindMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.pattern.FourOfAKindMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.pattern.ThreeOfAKindMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.PointsForEveryRingHit;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.PointsPerPatternHit;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.BellValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CherryValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CloverValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.DiamondValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.IronValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.LemonValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.SevenValueStackRing;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.RingAssetKeys;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.UiUtility;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRing extends Upgrade {

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

    public void draw(boolean isTouching) {
        Rectangle bounds = body.getBounds();

        float scale = body.getScale();
        float rotation = body.getRotation();

        float alpha = body.getAlpha();
        Vector2 position = body.getRenderPos(new Vector2());

        Color shadowColor = Assets.I().shadowColor();
        Vector2 shadowOffset = UiUtility.calcShadowOffset(body.getCardCenter());
        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().get(keySet().getShadowKey()),
            new Rectangle(position.x + shadowOffset.x, position.y - (isTouching ? 0.2f : 0.1f),
                bounds.width, bounds.height
            ), scale, rotation, isTouching ? ZIndex.RING_BAR_DRAGGING : ZIndex.RING_BAR, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(0.25f, alpha))));
        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().get(keySet().getTextureKey()),
            new Rectangle(position.x, position.y, bounds.width, bounds.height),
            scale, rotation, isTouching ? ZIndex.RING_BAR_DRAGGING : ZIndex.RING_BAR, new Color(1f, 1f, 1f, alpha)
        ));
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

    public static AbstractRing randomRing() {
        return instantiateCard(allRingClasses.get((int) (Math.random() * allRingClasses.size())));
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
