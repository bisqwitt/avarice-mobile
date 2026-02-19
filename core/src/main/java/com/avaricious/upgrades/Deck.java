package com.avaricious.upgrades;

import com.avaricious.cards.Card;
import com.avaricious.cards.ConvertPointsToArmorCard;
import com.avaricious.cards.DrawAndDiscardOneCard;
import com.avaricious.cards.FivePointsCard;
import com.avaricious.cards.FivePointsForEachCardInHandCard;
import com.avaricious.cards.OneDollarCard;
import com.avaricious.cards.TenArmorCard;
import com.avaricious.cards.TwoMultCard;
import com.avaricious.utility.Observable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck extends Observable<List<? extends Card>> {

    private static Deck instance;

    public static Deck I() {
        return instance == null ? instance = new Deck() : instance;
    }

    private Deck() {
//        allUpgrades.addAll(Arrays.asList(
//            CriticalHitDamageUpgrade.class,
//            DeptUpgrade.class,
//            RandomMultAdditionUpgrade.class,
//            MultPerEmptyJokerSlotUpgrade.class,
//            ThreeOfAKindMultAdditionUpgrade.class,
//            FourOfAKindMultAdditionUpgrade.class,
//            FiveOfAKindMultAdditionUpgrade.class,
//            PointsPerStreak.class,
//            LemonValueStackUpgrade.class,
//            CherryValueStackUpgrade.class,
//            CloverValueStackUpgrade.class,
//            BellValueStackUpgrade.class,
//            IronValueStackUpgrade.class,
//            DiamondValueStackUpgrade.class,
//            SevenValueStackUpgrade.class,
//            PointsPerConsecutiveHit.class
//        ));

        allCardClasses.addAll(Arrays.asList(
            FivePointsCard.class,
            TwoMultCard.class,
            TenArmorCard.class,
            OneDollarCard.class,
            ConvertPointsToArmorCard.class,
            FivePointsForEachCardInHandCard.class,
            DrawAndDiscardOneCard.class
        ));

        for (Class<? extends Card> cardClass : allCardClasses) {
            addUpgradeToDeck(instantiateCard(cardClass));
        }
    }

    private final List<Class<? extends Card>> allCardClasses = new ArrayList<>();
    private final List<Card> deck = new ArrayList<>();

    public List<? extends Card> randomUpgrades() {
        List<Class<? extends Card>> randomUpgrades = Arrays.asList(
            randomUpgradeClass(),
            randomUpgradeClass(),
            randomUpgradeClass()
        );
        List<Card> result = new ArrayList<>(randomUpgrades.size());
        for (int i = 0; i < randomUpgrades.size(); i++) {
            Class<? extends Card> upgradeClass = randomUpgrades.get(i);
            try {
                Card upgrade = upgradeClass.getDeclaredConstructor().newInstance();
                result.add(upgrade);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private Card instantiateCard(Class<? extends Card> cardClass) {
        try {
            return cardClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<? extends Card> randomUpgradeClass() {
        return allCardClasses.get((int) (Math.random() * allCardClasses.size()));
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
        for (Card upgrade : deck) {
            if (upgradeClass.isInstance(upgrade)) return (T) upgrade;
        }
        return null;
    }

    public Card pickCardFromDeck() {
//        return deck.get((int) (Math.random() * deck.size()));
        return instantiateCard(allCardClasses.get((int) (Math.random() * allCardClasses.size())));
    }

    public boolean upgradeIsInDeck(Class<? extends Card> upgradeClass) {
        for (Card upgrade : deck) {
            if (upgradeClass.isInstance(upgrade)) return true;
        }
        return false;
    }

    public void addUpgradeToDeck(Card upgrade) {
        deck.add(upgrade);
        notifyChanged(snapshot());
    }

    public void removeUpgrade(Card upgrade) {
        deck.remove(upgrade);
        notifyChanged(snapshot());
    }


    public List<Card> getDeck() {
        return deck;
    }

    public boolean spaceInDeck() {
        return deck.size() < 7;
    }

    @Override
    protected List<? extends Card> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(deck));
    }
}
