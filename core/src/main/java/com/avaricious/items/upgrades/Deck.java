package com.avaricious.items.upgrades;

import static com.avaricious.items.upgrades.cards.AbstractCard.allCardClasses;
import static com.avaricious.items.upgrades.cards.AbstractCard.instantiateItem;

import com.avaricious.DevTools;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.items.upgrades.cards.MultiCard;
import com.avaricious.items.upgrades.cards.PointsCard;
import com.avaricious.items.upgrades.cards.TriesCard;
import com.avaricious.utility.Observable;
import com.avaricious.utility.Seq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck extends Observable<List<? extends AbstractCard>> {

    private static Deck instance;

    public static Deck I() {
        return instance == null ? instance = new Deck() : instance;
    }

    private Deck() {
        notifyChanged(snapshot());
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
            addCardToDeck(instantiateItem(TriesCard.class));
            addCardToDeck(instantiateItem(TriesCard.class));
        }
    }

    private final List<AbstractCard> deck = new ArrayList<>();

    public AbstractCard drawRandomCard() {
        return removeCard((int) (Math.random() * deck.size()));
    }

    public AbstractCard drawCard(Class<? extends AbstractCard> cardClass) {
        return removeCard(deck.indexOf(Seq.of(deck)
            .filter(cardClass::isInstance)
            .findFirstOrNull()));
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

    public void removeCard(AbstractCard card) {
        deck.remove(card);
        notifyChanged(snapshot());
    }

    @Override
    protected List<? extends AbstractCard> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(deck));
    }

    public void setDeck(List<? extends AbstractCard> cardsInDeck) {
        deck.clear();
        Seq.of(cardsInDeck).forEach(this::addCardToDeck);
    }

    public List<AbstractCard> getDeck() {
        return deck;
    }
}
