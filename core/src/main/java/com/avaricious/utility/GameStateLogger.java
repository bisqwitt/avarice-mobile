package com.avaricious.utility;

import com.avaricious.components.ItemBag;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.items.upgrades.quests.AbstractQuest;
import com.avaricious.items.upgrades.quests.PlaySevenCardsInOneSpinQuest;

import java.util.ArrayList;
import java.util.List;

public class GameStateLogger {

    private static GameStateLogger instance;

    public static GameStateLogger I() {
        return instance == null ? instance = new GameStateLogger() : instance;
    }

    private GameStateLogger() {
    }

    private final List<AbstractCard> playedCardsThisRound = new ArrayList<>();
    private boolean defenceTypeCardsDisabled = false;
    private boolean cardsDisabledOnNoArmor = false;

    public void onNewRound() {
        playedCardsThisRound.clear();
        defenceTypeCardsDisabled = false;
        cardsDisabledOnNoArmor = false;
    }

    public void onCardPlayed(AbstractCard card) {
        playedCardsThisRound.add(card);

        if (ItemBag.I().containsItem(PlaySevenCardsInOneSpinQuest.class) && playedCardsThisRound.size() == 7) {
            ItemBag.I().getItemOfType(PlaySevenCardsInOneSpinQuest.class).stream()
                .filter(quest -> !quest.isCompleted())
                .forEach(AbstractQuest::complete);
        }
    }

    public List<AbstractCard> getPlayedCardsThisRound() {
        return playedCardsThisRound;
    }

    public void disableDefenceTypeCards() {
        defenceTypeCardsDisabled = true;
    }

    public boolean defenceTypeCardsDisabled() {
        return defenceTypeCardsDisabled;
    }

    public void disableCardsOnNoArmor() {
        cardsDisabledOnNoArmor = true;
    }

    public boolean cardsDisabledOnNoArmor() {
        return cardsDisabledOnNoArmor;
    }

}
