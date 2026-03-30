package com.avaricious.upgrades;

import static com.avaricious.upgrades.cards.AbstractCard.allCardClasses;
import static com.avaricious.upgrades.cards.AbstractCard.instantiateCard;

import com.avaricious.DevTools;
import com.avaricious.upgrades.cards.AbstractCard;
import com.avaricious.upgrades.cards.ArmorCard;
import com.avaricious.upgrades.cards.MultiCard;
import com.avaricious.upgrades.cards.PointsCard;
import com.avaricious.utility.Observable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck extends Observable<List<? extends AbstractCard>> {

    private static Deck instance;

    public static Deck I() {
        return instance == null ? instance = new Deck() : instance;
    }

    private Deck() {
        if (DevTools.allCardsInDeck()) {
            for (Class<? extends AbstractCard> cardClass : allCardClasses) {
                addCardToDeck(instantiateCard(cardClass));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                addCardToDeck(instantiateCard(PointsCard.class));
            }
            for (int i = 0; i < 3; i++) {
                addCardToDeck(instantiateCard(MultiCard.class));
            }
            addCardToDeck(instantiateCard(ArmorCard.class));
            addCardToDeck(instantiateCard(ArmorCard.class));
        }
    }

    private final List<AbstractCard> deck = new ArrayList<>();

    public AbstractCard drawRandomCard() {
        return removeCard((int) (Math.random() * deck.size()));
    }

    public AbstractCard drawCard(Class<? extends AbstractCard> cardClass) {
        return removeCard(deck.indexOf(deck.stream()
            .filter(cardClass::isInstance)
            .findFirst().get()));
    }

    public void addCardToDeck(AbstractCard upgrade) {
        deck.add(upgrade);
        notifyChanged(snapshot());
    }

    public AbstractCard removeCard(int index) {
        AbstractCard card = deck.remove(index);
        notifyChanged(snapshot());
        return card;
    }

    @Override
    protected List<? extends AbstractCard> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(deck));
    }
}
