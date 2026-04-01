package com.avaricious.items.upgrades;

import static com.avaricious.items.upgrades.cards.AbstractCard.allCardClasses;
import static com.avaricious.items.upgrades.cards.AbstractCard.instantiateItem;

import com.avaricious.DevTools;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.items.upgrades.cards.ArmorCard;
import com.avaricious.items.upgrades.cards.MultiCard;
import com.avaricious.items.upgrades.cards.PointsCard;
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
                addCardToDeck(instantiateItem(cardClass));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                addCardToDeck(instantiateItem(PointsCard.class));
            }
            for (int i = 0; i < 3; i++) {
                addCardToDeck(instantiateItem(MultiCard.class));
            }
            addCardToDeck(instantiateItem(ArmorCard.class));
            addCardToDeck(instantiateItem(ArmorCard.class));
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
