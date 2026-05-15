package com.avaricious.screens;

import com.avaricious.Main;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.texts.PlayerText;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.ZIndex;
import com.avaricious.utility.playerRun.PlayerRunManager;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerCombatScreen extends ScreenAdapter {

    private final Main app;

    private final PlayerText playerText = new PlayerText(
        new Vector2(2, 16), 15f, 0.1f, ZIndex.SLOT_MACHINE
    );

    private final DigitalNumber playerHealth = new DigitalNumber(
        PlayerRunManager.I().getPlayerRun().playerHealth, Assets.I().healthRedColor(),
        new Rectangle(2, 15, 7 / 10f, 11 / 10f), 1
    );

    private final DigitalNumber playerScore = new DigitalNumber(
        500, Assets.I().lightColor(),
        new Rectangle(2f, 14f, 7 / 10f, 11 / 10f), 1
    );

    public PlayerCombatScreen(Main app) {
        this.app = app;
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        batch.setProjectionMatrix(app.getViewport().getCamera().combined);

        playerText.draw(delta);
        playerHealth.draw(delta);
        playerScore.draw(delta);

        batch.begin();
        Pencil.I().draw(batch);
        batch.end();
    }
}
