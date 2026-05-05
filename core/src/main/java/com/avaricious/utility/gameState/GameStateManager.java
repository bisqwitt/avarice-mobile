package com.avaricious.utility.gameState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class GameStateManager {

    private static GameStateManager instance;

    public static GameStateManager I() {
        return instance == null ? instance = new GameStateManager() : instance;
    }

    private GameStateManager() {
        file = Gdx.files.local("gamestate.json");
        loadGameState();
    }

    private final Json json = new Json();

    private final FileHandle file;
    private GameState gameState;

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

    private void loadGameState() {
        if (!file.exists() || file.length() == 0) {
            gameState = new GameState();
            saveGameState();
            return;
        }
        gameState = json.fromJson(GameState.class, file.readString());
    }

    private void saveGameState() {
        String data = json.prettyPrint(gameState);
        file.writeString(data, false);
    }

    public void onCurrentRoundChange(int currentRound) {
        gameState.currentRound = currentRound;
    }

    public void onCreditChange(int credit) {
        gameState.credits = credit;
    }

}
