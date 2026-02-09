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
import com.avaricious.utility.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpgradesManager {

    private static UpgradesManager instance;

    public static UpgradesManager I() {
        return instance == null ? instance = new UpgradesManager() : instance;
    }

    private UpgradesManager() {
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
            addUpgrade(upgrade);
        }
    }

    private final List<Class<? extends Upgrade>> allUpgrades = new ArrayList<>();
    private final List<Upgrade> deck = new ArrayList<>();
    private final List<Listener<List<? extends Upgrade>>> listeners = new CopyOnWriteArrayList<>();

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
        return upgradeIsOwned(upgradeClass) ? randomUpgradeClassNotOwned() : upgradeClass;
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

    public boolean upgradeIsOwned(Class<? extends Upgrade> upgradeClass) {
        for(Upgrade upgrade : deck) {
            if(upgradeClass.isInstance(upgrade)) return true;
        }
        return false;
    }

    public void addUpgrade(Upgrade upgrade) {
        deck.add(upgrade);
        notifyDeckChanged(deck);
    }

    public void removeUpgrade(Upgrade upgrade) {
        deck.remove(upgrade);
    }

    public AutoCloseable onDeckChange(Listener<List<? extends Upgrade>> listener) {
        listeners.add(listener);

        // immediate push of current state
        listener.accept(deck);

        return () -> listeners.remove(listener);
    }

    private void notifyDeckChanged(List<? extends Upgrade> deck) {
        // publish an immutable snapshot to avoid external mutation
        List<? extends Upgrade> currentDeck = Collections.unmodifiableList(deck);
        for(Listener<List<? extends Upgrade>> listener : listeners) {
            listener.accept(currentDeck);
        }
    }


    public List<Upgrade> getDeck() {
        return deck;
    }

    public boolean spaceInDeck() {
        return deck.size() < 7;
    }
}
