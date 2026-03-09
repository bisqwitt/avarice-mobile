package com.avaricious.components;

import com.avaricious.upgrades.CriticalHitDamageRing;
import com.avaricious.upgrades.DeptRing;
import com.avaricious.upgrades.DoubleXpRing;
import com.avaricious.upgrades.RandomMultAdditionRing;
import com.avaricious.upgrades.Ring;
import com.avaricious.upgrades.multAdditions.MultPerEmptyJokerSlotRing;
import com.avaricious.upgrades.multAdditions.pattern.FiveOfAKindMultAdditionRing;
import com.avaricious.upgrades.multAdditions.pattern.FourOfAKindMultAdditionRing;
import com.avaricious.upgrades.multAdditions.pattern.ThreeOfAKindMultAdditionRing;
import com.avaricious.upgrades.pointAdditions.PointsPerConsecutiveHit;
import com.avaricious.upgrades.pointAdditions.PointsPerStreak;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.BellValueStackRing;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.CherryValueStackRing;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.CloverValueStackRing;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.DiamondValueStackRing;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.IronValueStackRing;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.LemonValueStackRing;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.SevenValueStackRing;
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

    private final List<Ring> rings = new ArrayList<>();

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

    public void addRelic(Ring ring) {
        rings.add(ring);
    }

    public Ring randomRelic() {
        return instantiateRelic(allRelicClasses().get((int) (Math.random() * allRelicClasses().size())));
    }

    public List<Class<? extends Ring>> allRelicClasses() {
        return Arrays.asList(
            CriticalHitDamageRing.class,
            DeptRing.class,
            RandomMultAdditionRing.class,
            MultPerEmptyJokerSlotRing.class,
            ThreeOfAKindMultAdditionRing.class,
            FourOfAKindMultAdditionRing.class,
            FiveOfAKindMultAdditionRing.class,
            PointsPerStreak.class,
            LemonValueStackRing.class,
            CherryValueStackRing.class,
            CloverValueStackRing.class,
            BellValueStackRing.class,
            IronValueStackRing.class,
            DiamondValueStackRing.class,
            SevenValueStackRing.class,
            PointsPerConsecutiveHit.class,
            DoubleXpRing.class
        );
    }

    private Ring instantiateRelic(Class<? extends Ring> relicClass) {
        try {
            return relicClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
