package com.avaricious.components;

import com.avaricious.upgrades.rings.AbstractRing;
import com.avaricious.upgrades.rings.CriticalDamageRing;
import com.avaricious.upgrades.rings.DeptRing;
import com.avaricious.upgrades.rings.DoubleXpRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.MultiPerEmptyRingSlotRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.RandomMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.pattern.FiveOfAKindMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.pattern.FourOfAKindMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.pattern.ThreeOfAKindMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.PointsPerPatternHit;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.BellValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CherryValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CloverValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.DiamondValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.IronValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.LemonValueStackRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.SevenValueStackRing;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelicBag {

    private static RelicBag instance;

    public static RelicBag I() {
        return instance == null ? instance = new RelicBag() : instance;
    }

    private RelicBag() {
    }

    private final Rectangle bagBounds = new Rectangle(4.75f, 0.4f, 42 / 25f, 48 / 25f);

    private final TextureRegion bagTexture = Assets.I().get(AssetKey.BAG);
    private final TextureRegion shadowTexture = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final List<AbstractRing> rings = new ArrayList<>();

    public void draw(SpriteBatch batch) {
        Pencil.I().addDrawing(new TextureDrawing(
            shadowTexture,
            new Rectangle(bagBounds.x - 0.2f, bagBounds.y - 0.2f, bagBounds.width + 0.4f, bagBounds.height + 0.4f),
            ZIndex.RELIC_BAG, Assets.I().shadowColor()));

        Pencil.I().addDrawing(new TextureDrawing(
            bagTexture,
            bagBounds,
            ZIndex.RELIC_BAG
        ));
    }

    public void addRelic(AbstractRing ring) {
        rings.add(ring);
    }

    public AbstractRing randomRelic() {
        return instantiateRelic(allRingClasses().get((int) (Math.random() * allRingClasses().size())));
    }

    public List<Class<? extends AbstractRing>> allRingClasses() {
        return Arrays.asList(
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
            DoubleXpRing.class
        );
    }

    private AbstractRing instantiateRelic(Class<? extends AbstractRing> relicClass) {
        try {
            return relicClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
