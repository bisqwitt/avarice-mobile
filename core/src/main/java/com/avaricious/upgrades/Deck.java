package com.avaricious.upgrades;

import com.avaricious.upgrades.multAdditions.MultPerEmptyJokerSlotUpgrade;
import com.avaricious.upgrades.multAdditions.pattern.FiveOfAKindMultAdditionUpgrade;
import com.avaricious.upgrades.multAdditions.pattern.FourOfAKindMultAdditionUpgrade;
import com.avaricious.upgrades.multAdditions.pattern.ThreeOfAKindMultAdditionUpgrade;
import com.avaricious.upgrades.pointAdditions.PointsPerConsecutiveHit;
import com.avaricious.upgrades.pointAdditions.PointsPerStreak;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.BellValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.CherryValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.CloverValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.DiamondValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.IronValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.LemonValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.SevenValueStackUpgrade;
import com.avaricious.utility.Observable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck extends Observable<List<? extends Upgrade>> {

    private static Deck instance;

    public static Deck I() {
        return instance == null ? instance = new Deck() : instance;
    }

    private Deck() {
        allUpgrades.addAll(Arrays.asList(
            CriticalHitDamageUpgrade.class,
            DeptUpgrade.class,
            RandomMultAdditionUpgrade.class,
            MultPerEmptyJokerSlotUpgrade.class,
            ThreeOfAKindMultAdditionUpgrade.class,
            FourOfAKindMultAdditionUpgrade.class,
            FiveOfAKindMultAdditionUpgrade.class,
            PointsPerStreak.class,
            LemonValueStackUpgrade.class,
            CherryValueStackUpgrade.class,
            CloverValueStackUpgrade.class,
            BellValueStackUpgrade.class,
            IronValueStackUpgrade.class,
            DiamondValueStackUpgrade.class,
            SevenValueStackUpgrade.class,
            PointsPerConsecutiveHit.class
        ));

        for(Upgrade upgrade : randomUpgrades()) {
            addUpgradeToDeck(upgrade);
        }
    }

    private final List<Class<? extends Upgrade>> allUpgrades = new ArrayList<>();
    private final List<Upgrade> deck = new ArrayList<>();

    public List<? extends Upgrade> randomUpgrades() {
        List<Class<? extends Upgrade>> randomUpgrades = Arrays.asList(
            randomUpgradeClassNotOwned(),
            randomUpgradeClassNotOwned(),
            randomUpgradeClassNotOwned()
        );
        List<Upgrade> result = new ArrayList<>(randomUpgrades.size());
        for (int i = 0; i < randomUpgrades.size(); i++) {
            Class<? extends Upgrade> upgradeClass = randomUpgrades.get(i);
            try {
                Upgrade upgrade =
                    upgradeClass.getDeclaredConstructor(UpgradeRarity.class)
                        .newInstance(UpgradeRarity.COMMON);
                result.add(upgrade);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private Class<? extends Upgrade> randomUpgradeClassNotOwned() {
        Class<? extends Upgrade> upgradeClass = allUpgrades.get((int) (Math.random() * allUpgrades.size()));
        return upgradeIsInDeck(upgradeClass) ? randomUpgradeClassNotOwned() : upgradeClass;
    }

    public <T> List<T> getUpgradesOfClass(Class<T> clazz) {
        List<T> out = new ArrayList<>();
        for (int i = 0; i < deck.size(); i++) {          // deck is a List<?>
            Object o = deck.get(i);
            if (clazz.isInstance(o)) {
                out.add(clazz.cast(o));
            }
        }
        return out;
    }

    public <T> T getUpgradeOfClass(Class<T> upgradeClass) {
        for(Upgrade upgrade : deck) {
            if(upgradeClass.isInstance(upgrade)) return (T) upgrade;
        }
        return null;
    }

    public Upgrade pickUpgradefromDeck() {
        return deck.isEmpty() ? null : deck.remove(0);
    }

    public boolean upgradeIsInDeck(Class<? extends Upgrade> upgradeClass) {
        for(Upgrade upgrade : deck) {
            if(upgradeClass.isInstance(upgrade)) return true;
        }
        return false;
    }

    public void addUpgradeToDeck(Upgrade upgrade) {
        deck.add(upgrade);
        notifyChanged(snapshot());
    }

    public void removeUpgrade(Upgrade upgrade) {
        deck.remove(upgrade);
        notifyChanged(snapshot());
    }


    public List<Upgrade> getDeck() {
        return deck;
    }

    public boolean spaceInDeck() {
        return deck.size() < 7;
    }

    @Override
    protected List<? extends Upgrade> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(deck));
    }
}
