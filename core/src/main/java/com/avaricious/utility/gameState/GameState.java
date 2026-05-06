package com.avaricious.utility.gameState;

import com.avaricious.components.HealthState;
import com.avaricious.components.roundInfoPanel.ScoreState;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public HealthState healthState;
    public ScoreState scoreState;

    public int currentRound;
    public int credits;

    public List<String> cardsInDeck = new ArrayList<>();
    public List<String> cardsInHand = new ArrayList<>();
    public List<String> rings = new ArrayList<>();
    public List<String> items = new ArrayList<>();

}
