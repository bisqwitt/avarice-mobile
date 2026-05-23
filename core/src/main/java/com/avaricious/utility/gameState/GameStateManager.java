package com.avaricious.utility.gameState;

import com.avaricious.CreditManager;
import com.avaricious.DevTools;
import com.avaricious.RoundsManager;
import com.avaricious.components.ItemBag;
import com.avaricious.components.RingBar;
import com.avaricious.components.roundInfoPanel.RoundInfoPanel;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.items.AbstractItem;
import com.avaricious.items.upgrades.Deck;
import com.avaricious.items.upgrades.Hand;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.items.upgrades.rings.AbstractRing;
import com.avaricious.utility.Listener;
import com.avaricious.utility.Seq;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.List;

public class GameStateManager {

    private static GameStateManager instance;

    public static GameStateManager I() {
        return instance == null ? instance = new GameStateManager() : instance;
    }

    private GameStateManager() {
        file = Gdx.files.local(GAME_STATE_FILE_NAME);

        observeValue(RoundInfoPanel.I()::onChange, tries -> gameState.tries = tries.intValue());
        observeValue(ScoreDisplay.I()::onChange, scoreState -> gameState.scoreState = scoreState);
        observeValue(RoundsManager.I()::onChange, currentRound -> gameState.currentRound = currentRound);
        observeValue(CreditManager.I()::onChange, credits -> gameState.credits = credits);

        Deck.I().onChange(cards -> copyIds(cards, gameState.cardsInDeck));
        Hand.I().onChange(cards -> copyIds(cards, gameState.cardsInHand));
        RingBar.I().onChange(rings -> copyIds(rings, gameState.rings));
        ItemBag.I().onChange(items -> copyIds(items, gameState.items));

        loadGameState();
    }

    public static final String GAME_STATE_FILE_NAME = "gamestate.json";

    private final FileHandle file;
    private GameState gameState = new GameState();

    private final Json json = new Json();

    private boolean appliedLoadedState = false;
    private boolean applyingLoadedState = false;

    private boolean isDirty = false;
    private float saveTimer = 0f;

    public void update(float delta) {
        if (!isDirty) return;

        saveTimer += delta;

        if (saveTimer >= 1f) {
            saveGameState();
            isDirty = false;
            saveTimer = 0f;
        }
    }

    public void applyLoadedGameState() {
        applyingLoadedState = true;

        RoundInfoPanel.I().setSpins(gameState.tries);
        ScoreDisplay.I().setScoreState(gameState.scoreState);
        RoundsManager.I().setCurrentRound(gameState.currentRound);
        CreditManager.I().setCredits(gameState.credits);

        Deck.I().setDeck(instantiateItems(gameState.cardsInDeck, AbstractCard.class));
        Hand.I().setCards(instantiateItems(gameState.cardsInHand, AbstractCard.class));
        RingBar.I().setRings(instantiateItems(gameState.rings, AbstractRing.class));
        ItemBag.I().setItems(instantiateItems(gameState.items, AbstractItem.class));

        applyingLoadedState = false;
        appliedLoadedState = true;
    }

    private void loadGameState() {
        if (!file.exists() || file.length() == 0 || DevTools.rewriteSaveFile()) {
            gameState = new GameState();
            saveGameState();
            return;
        }
        gameState = json.fromJson(GameState.class, file.readString());
    }

    private void saveGameState() {
        String data = json.prettyPrint(gameState);
        file.writeString(data, false);

//        PlayerRunManager.I().updatePlayerRun(gameState);
    }

    private <T> void observeValue(
        Listener<Listener<T>> register,
        Listener<T> setter
    ) {
        register.accept(value -> {
            setter.accept(value);
            markDirty();
        });
    }

    private void copyIds(
        List<? extends AbstractItem> source,
        List<String> target
    ) {
        target.clear();
        Seq.of(source).forEach(item -> target.add(item.getId()));
        markDirty();
    }

    private void markDirty() {
        if (!applyingLoadedState) isDirty = true;
    }

    private <T extends AbstractItem> List<T> instantiateItems(
        List<String> ids,
        Class<T> type
    ) {
        return Seq.of(ids)
            .map(id -> AbstractItem.instantiateItem(id, type))
            .toList();
    }

    public boolean appliedLoadedState() {
        return appliedLoadedState;
    }

}
