package com.avaricious.components;

import com.avaricious.upgrades.CriticalHitDamageRelic;
import com.avaricious.upgrades.DeptRelic;
import com.avaricious.upgrades.RandomMultAdditionRelic;
import com.avaricious.upgrades.Relic;
import com.avaricious.upgrades.multAdditions.MultPerEmptyJokerSlotRelic;
import com.avaricious.upgrades.multAdditions.pattern.FiveOfAKindMultAdditionRelic;
import com.avaricious.upgrades.multAdditions.pattern.FourOfAKindMultAdditionRelic;
import com.avaricious.upgrades.multAdditions.pattern.ThreeOfAKindMultAdditionRelic;
import com.avaricious.upgrades.pointAdditions.PointsPerConsecutiveHit;
import com.avaricious.upgrades.pointAdditions.PointsPerStreak;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.BellValueStackRelic;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.CherryValueStackRelic;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.CloverValueStackRelic;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.DiamondValueStackRelic;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.IronValueStackRelic;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.LemonValueStackRelic;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.SevenValueStackRelic;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
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

    private final List<Relic> relics = new ArrayList<>();

    public void draw(SpriteBatch batch) {
        Pencil.I().addDrawing(new TextureDrawing(
            shadowTexture,
            new Rectangle(bagBounds.x - 0.2f, bagBounds.y - 0.2f, bagBounds.width + 0.4f, bagBounds.height + 0.4f),
            7, Assets.I().shadowColor()));

        Pencil.I().addDrawing(new TextureDrawing(
            bagTexture,
            bagBounds,
            7
        ));
    }

    public void addRelic(Relic relic) {
        relics.add(relic);
    }

    public Relic randomRelic() {
        return instantiateRelic(allRelicClasses().get((int) (Math.random() * allRelicClasses().size())));
    }

    public List<Class<? extends Relic>> allRelicClasses() {
        return Arrays.asList(
            CriticalHitDamageRelic.class,
            DeptRelic.class,
            RandomMultAdditionRelic.class,
            MultPerEmptyJokerSlotRelic.class,
            ThreeOfAKindMultAdditionRelic.class,
            FourOfAKindMultAdditionRelic.class,
            FiveOfAKindMultAdditionRelic.class,
            PointsPerStreak.class,
            LemonValueStackRelic.class,
            CherryValueStackRelic.class,
            CloverValueStackRelic.class,
            BellValueStackRelic.class,
            IronValueStackRelic.class,
            DiamondValueStackRelic.class,
            SevenValueStackRelic.class,
            PointsPerConsecutiveHit.class
        );
    }

    private Relic instantiateRelic(Class<? extends Relic> relicClass) {
        try {
            return relicClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
