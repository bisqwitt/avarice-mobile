package com.avaricious.utility.gameState;

import com.avaricious.items.AbstractItem;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.items.upgrades.rings.AbstractRing;

import java.util.List;

public class GameState {

    public int currentRound;
    public int credits;

    public List<AbstractCard> cardsInDeck;
    public List<AbstractCard> cardsInHand;
    public List<AbstractRing> rings;
    public List<AbstractItem> items;

}
