package com.avaricious.upgrades;

import com.avaricious.DevTools;
import com.avaricious.upgrades.cards.Card;
import com.avaricious.upgrades.cards.ConvertPointsToArmorCard;
import com.avaricious.upgrades.cards.DrawAndDiscardOneCard;
import com.avaricious.upgrades.cards.DrawTwoCardsForTenDamage;
import com.avaricious.upgrades.cards.EitherDoublePointsOrHalveMulti;
import com.avaricious.upgrades.cards.FiveMultForEveryCardDiscarded;
import com.avaricious.upgrades.cards.FivePointsCard;
import com.avaricious.upgrades.cards.FivePointsForEachCardInHandCard;
import com.avaricious.upgrades.cards.LifestealForEveryFruitHitCard;
import com.avaricious.upgrades.cards.OneDollarCard;
import com.avaricious.upgrades.cards.OnePointForEveryFruitCard;
import com.avaricious.upgrades.cards.TwentyArmorCard;
import com.avaricious.upgrades.cards.TwoMultCard;
import com.avaricious.upgrades.cards.TwoPointsForEverySymbolHit;
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
        allCardClasses.addAll(Arrays.asList(
            FivePointsCard.class,
            TwoMultCard.class,
            TwentyArmorCard.class,
            OneDollarCard.class,
            ConvertPointsToArmorCard.class,
            FivePointsForEachCardInHandCard.class,
            DrawAndDiscardOneCard.class,
            FiveMultForEveryCardDiscarded.class,
            DrawTwoCardsForTenDamage.class,
            TwoPointsForEverySymbolHit.class,
            OnePointForEveryFruitCard.class,
            EitherDoublePointsOrHalveMulti.class,
            LifestealForEveryFruitHitCard.class
        ));

        if (DevTools.allCardsInDeck) {
            for (Class<? extends Card> cardClass : allCardClasses) {
                addUpgradeToDeck(instantiateCard(cardClass));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                addUpgradeToDeck(instantiateCard(FivePointsCard.class));
            }
            for (int i = 0; i < 3; i++) {
                addUpgradeToDeck(instantiateCard(TwoMultCard.class));
            }
            addUpgradeToDeck(instantiateCard(TwentyArmorCard.class));
            addUpgradeToDeck(instantiateCard(TwentyArmorCard.class));
        }
    }

    private final List<Class<? extends Card>> allCardClasses = new ArrayList<>();
    private final List<Card> deck = new ArrayList<>();

    public List<? extends Card> randomUpgrades(int amount) {
        List<Card> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            result.add(randomUpgrade());
        }
        return result;
    }

    public Card randomUpgrade() {
        try {
            return randomUpgradeClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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

    public Card drawRandomCard() {
        return removeCard((int) (Math.random() * deck.size()));
    }

    public Card drawCard(Class<? extends Card> cardClass) {
        return removeCard(deck.indexOf(deck.stream()
            .filter(card -> cardClass.isInstance(card))
            .findFirst().get()));
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

    public Card removeCard(int index) {
        Card card = deck.remove(index);
        notifyChanged(snapshot());
        return card;
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
