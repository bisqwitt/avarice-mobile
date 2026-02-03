package com.avaricious.upgrades;

import com.avaricious.components.slot.Symbol;
import com.avaricious.upgrades.multAdditions.MultAdditionUpgrade;
import com.avaricious.upgrades.multAdditions.MultPerEmptyJokerSlotUpgrade;
import com.avaricious.upgrades.multAdditions.pattern.FiveOfAKindMultAdditionUpgrade;
import com.avaricious.upgrades.multAdditions.pattern.FourOfAKindMultAdditionUpgrade;
import com.avaricious.upgrades.multAdditions.pattern.ThreeOfAKindMultAdditionUpgrade;
import com.avaricious.upgrades.pointAdditions.PointAdditionUpgrade;
import com.avaricious.upgrades.pointAdditions.PointsPerStreak;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.BellValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.CherryValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.CloverValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.DiamondValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.IronValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.LemonValueStackUpgrade;
import com.avaricious.upgrades.pointAdditions.symbolValueStacker.SevenValueStackUpgrade;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            RetriggerUpgrade.class,
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
            SevenValueStackUpgrade.class)
        );

        randomUpgrades().forEach(this::addUpgrade);
        randomUpgrades().forEach(this::addUpgrade);
    }

    private final List<Class<? extends Upgrade>> allUpgrades = new ArrayList<>();
    private final List<Upgrade> deck = new ArrayList<>();
    private final List<Consumer<List<? extends Upgrade>>> listeners = new CopyOnWriteArrayList<>();

    public int multAdditions(List<Symbol> selection, long count) {
        return deck.stream()
            .filter(MultAdditionUpgrade.class::isInstance)
            .map(MultAdditionUpgrade.class::cast)
            .filter(upgrade -> upgrade.condition(selection, count))
            .mapToInt(MultAdditionUpgrade::getMulti)
            .sum();
    }

    public int chipAdditions(List<Symbol> selection, long count) {
        return deck.stream()
            .filter(PointAdditionUpgrade.class::isInstance)
            .map(PointAdditionUpgrade.class::cast)
            .filter(upgrade -> upgrade.condition(selection, count))
            .mapToInt(PointAdditionUpgrade::getPoints)
            .sum();
    }

    public List<? extends Upgrade> randomUpgrades() {
        List<Class<? extends Upgrade>> randomUpgrades = Arrays.asList(
            allUpgrades.get((int) (Math.random() * allUpgrades.size())),
            allUpgrades.get((int) (Math.random() * allUpgrades.size())),
            allUpgrades.get((int) (Math.random() * allUpgrades.size()))
        );
        return randomUpgrades.stream().map(upgradeClass -> {
            try {
                return upgradeClass.getDeclaredConstructor(UpgradeRarity.class).newInstance(UpgradeRarity.COMMON);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public <T> Stream<T> getUpgradesOfClass(Class<T> clazz) {
        return deck.stream()
            .filter(clazz::isInstance)
            .map(clazz::cast);
    }

    public void addUpgrade(Upgrade upgrade) {
        deck.add(upgrade);
        mergeDuplicates();

        notifyDeckChanged(deck);
    }

    private void mergeDuplicates() {
//        record Key(Class<?> type, UpgradeRarity rarity) {}
//
//        var dupKey = deck.stream()
//            .collect(java.util.stream.Collectors.groupingBy(
//                u -> new Key(u.getClass(), u.getRarity())
//            ))
//            .entrySet().stream()
//            .filter(e -> e.getValue().size() >= 2)
//            .map(java.util.Map.Entry::getKey)
//            .findFirst();
//
//        if (dupKey.isEmpty()) return;
//
//        var key = dupKey.get();
//        var pair = deck.stream()
//            .filter(u -> u.getClass() == key.type() && u.getRarity() == key.rarity())
//            .limit(2)
//            .toList();
//
//        if (pair.get(0).getRarity() == UpgradeRarity.LEGENDARY) {
//            return;
//        }
//
//        deck.remove(pair.get(1));
//        pair.get(0).increaseRarity();
//
//        mergeDuplicates();
    }

    public void removeUpgrade(Upgrade upgrade) {
        deck.remove(upgrade);
    }

    public AutoCloseable onDeckChange(Consumer<List<? extends Upgrade>> listener) {
        listeners.add(listener);

        // immediate push of current state
        listener.accept(deck);

        return () -> listeners.remove(listener);
    }

    private void notifyDeckChanged(List<? extends Upgrade> deck) {
        // publish an immutable snapshot to avoid external mutation
        List<? extends Upgrade> currentDeck = Collections.unmodifiableList(deck);
        listeners.forEach(l -> l.accept(currentDeck));
    }


    public List<Upgrade> getDeck() {
        return deck;
    }

    public boolean spaceInDeck() {
        return deck.size() < 7;
    }
}
