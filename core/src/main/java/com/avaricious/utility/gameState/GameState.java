package com.avaricious.utility.gameState;

import com.avaricious.components.roundInfoPanel.ScoreState;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public ScoreState scoreState;

    public int tries;
    public int currentRound;
    public int credits;

    public List<String> cardsInDeck = new ArrayList<>();
    public List<String> cardsInHand = new ArrayList<>();
    public List<String> rings = new ArrayList<>();
    public List<String> items = new ArrayList<>();

    public GameState copy() {
        GameState copy = new GameState();
        copy.scoreState = new ScoreState(
            this.scoreState.points,
            this.scoreState.multi,
            this.scoreState.streak
        );
        copy.tries = this.tries;
        copy.currentRound = this.currentRound;
        copy.credits = this.credits;
        copy.cardsInDeck.addAll(this.cardsInDeck);
        copy.cardsInHand.addAll(this.cardsInHand);
        copy.rings.addAll(this.rings);
        copy.items.addAll(this.items);

        return copy;
    }

}
