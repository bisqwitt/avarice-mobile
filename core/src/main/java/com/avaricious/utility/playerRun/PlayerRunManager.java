package com.avaricious.utility.playerRun;

import com.avaricious.DevTools;
import com.avaricious.network.NetworkController;
import com.avaricious.utility.gameState.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class PlayerRunManager {

    private static PlayerRunManager instance;

    public static PlayerRunManager I() {
        return instance == null ? instance = new PlayerRunManager() : instance;
    }

    private PlayerRunManager() {
        playerRunFile = Gdx.files.local(PLAYER_RUN_FILE_NAME);
        enemyRunFile = Gdx.files.local(ENEMY_RUN_FILE_NAME);
    }

    public static final String PLAYER_RUN_FILE_NAME = "playerRun.json";
    public static final String ENEMY_RUN_FILE_NAME = "enemyPlayerRun.json";

    private final FileHandle playerRunFile;
    private final FileHandle enemyRunFile;
    private final PlayerRun playerRun = new PlayerRun();
    private PlayerRun enemyRun;

    private final Json json = new Json();

    private boolean isDirty = false;
    private float saveTimer = 0f;

    public void update(float delta) {
        if (!isDirty) return;

        saveTimer += delta;

        if (saveTimer >= 1f) {
            savePlayerRun();
            isDirty = false;
            saveTimer = 0f;
        }
    }

    private void savePlayerRun() {
        String data = json.prettyPrint(playerRun);
        playerRunFile.writeString(data, false);
    }

    public void updatePlayerRun(GameState newGameState) {
        playerRun.gameStates.add(newGameState.copy());
        isDirty = true;
    }

    public void updatePlayerRoundEndScore(int round, int score) {
        playerRun.roundEndScores.add(new PlayerRoundEndScore(round, score));
        NetworkController.I().match().sendRoundEndScore(round, score);
    }

    public PlayerRun getPlayerRun() {
        return playerRun;
    }

    public PlayerRun getEnemyRun() {
        if (enemyRun == null) {
            if (enemyRunFile.exists() && !DevTools.rewriteSaveFile())
                enemyRun = json.fromJson(PlayerRun.class, enemyRunFile.readString());
            else {
                enemyRun = defaultEnemyRun();
                String data = json.prettyPrint(enemyRun);
                enemyRunFile.writeString(data, false);
            }
        }
        return enemyRun;
    }

    private PlayerRun defaultEnemyRun() {
        PlayerRun enemyRun = new PlayerRun();
        enemyRun.roundEndScores.add(new PlayerRoundEndScore(1, 200));
        enemyRun.roundEndScores.add(new PlayerRoundEndScore(2, 350));
        enemyRun.roundEndScores.add(new PlayerRoundEndScore(3, 450));
        return enemyRun;
    }

}
