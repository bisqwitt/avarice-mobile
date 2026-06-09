package com.avaricious.items.upgrades;

import static com.avaricious.items.upgrades.cards.AbstractCard.allCardClasses;
import static com.avaricious.items.upgrades.cards.AbstractCard.instantiateItem;

import com.avaricious.DevTools;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.items.upgrades.cards.SymbolTriggerCard.BellTriggerCard;
import com.avaricious.items.upgrades.cards.SymbolTriggerCard.CherryTriggerCard;
import com.avaricious.items.upgrades.cards.SymbolTriggerCard.CloverTriggerCard;
import com.avaricious.items.upgrades.cards.SymbolTriggerCard.DiamondTriggerCard;
import com.avaricious.items.upgrades.cards.SymbolTriggerCard.IronTriggerCard;
import com.avaricious.items.upgrades.cards.SymbolTriggerCard.LemonTriggerCard;
import com.avaricious.items.upgrades.cards.SymbolTriggerCard.SevenTriggerCard;
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
            addCardToDeck(instantiateItem(LemonTriggerCard.class));
            addCardToDeck(instantiateItem(CherryTriggerCard.class));
            addCardToDeck(instantiateItem(CloverTriggerCard.class));
            addCardToDeck(instantiateItem(BellTriggerCard.class));
            addCardToDeck(instantiateItem(IronTriggerCard.class));
            addCardToDeck(instantiateItem(DiamondTriggerCard.class));
            addCardToDeck(instantiateItem(SevenTriggerCard.class));
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
