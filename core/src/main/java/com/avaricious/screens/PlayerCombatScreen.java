package com.avaricious.screens;

import com.avaricious.Main;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.ScreenShake;
import com.avaricious.components.texts.EnemyText;
import com.avaricious.components.texts.PlayerText;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.ZIndex;
import com.avaricious.utility.playerRun.PlayerRunManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;

public class PlayerCombatScreen extends ScreenAdapter {

    private final Main app;

    private final ScreenShake screenShake;

    private final PlayerText playerText = new PlayerText(
        new Vector2(2, 16.5f), 12f, 0.1f, ZIndex.SLOT_MACHINE
    );
    private final DigitalNumber playerHealth = new DigitalNumber(
        PlayerRunManager.I().getPlayerRun().playerHealth, Assets.I().healthRedColor(),
        new Rectangle(2, 15, 7 / 10f, 11 / 10f), 1
    );
    private final DigitalNumber playerScore = new DigitalNumber(
        500, Assets.I().lightColor(),
        new Rectangle(2f, 12.5f, 7 / 10f, 11 / 10f), 1
    );

    private final EnemyText enemyText = new EnemyText(
        new Vector2(2f, 5f), 12f, 0.1f, ZIndex.SLOT_MACHINE
    );
    private final DigitalNumber enemyHealth = new DigitalNumber(
        PlayerRunManager.I().getEnemyRun().playerHealth, Assets.I().healthRedColor(),
        new Rectangle(2, 3.5f, 7 / 10f, 11 / 10f), 1
    );
    private final DigitalNumber enemyScore = new DigitalNumber(
        0, Assets.I().lightColor(),
        new Rectangle(2f, 7.5f, 7 / 10f, 11 / 10f), 1
    );

    private boolean playerScoreIsHigher = false;

    private boolean movingScores = false;
    private float moveTimer = 0f;
    private float moveDuration = 1f;

    private final Vector2 playerScoreStart = new Vector2();
    private final Vector2 playerScoreTarget = new Vector2();

    private final Vector2 enemyScoreStart = new Vector2();
    private final Vector2 enemyScoreTarget = new Vector2();

    private boolean lerpingScores = false;
    private float lerpTimer = 0f;
    private float lerpDuration = 1f;

    private DigitalNumber lerpTargetNumber1, lerpTargetNumber2;
    private float lerpStart1, lerpTarget1, lerpStart2, lerpTarget2;

    public PlayerCombatScreen(Main app) {
        this.app = app;
        screenShake = ScreenShake.I().setCameras(app.getViewport().getCamera(), app.getUiViewport().getCamera());
    }

    @Override
    public void show() {
        playerHealth.setValue(PlayerRunManager.I().getPlayerRun().playerHealth);
        playerScore.setValue(PlayerRunManager.I().getPlayerRun().getLastRoundEndScore());
//        playerScore.setValue(500);

        enemyHealth.setValue(PlayerRunManager.I().getEnemyRun().playerHealth);
        enemyScore.setValue(PlayerRunManager.I().getEnemyRun().getLastRoundEndScore());

        startAnimation();
    }

    private void update(float delta) {
        screenShake.update(delta);

        if (movingScores) {
            moveTimer += delta;

            float progress = Math.min(moveTimer / moveDuration, 1f);
            updateMove(playerScore, playerScoreStart, playerScoreTarget, progress);
            updateMove(enemyScore, enemyScoreStart, enemyScoreTarget, progress);
            if (progress >= 1f) {
                movingScores = false;
            }
        }

        if (lerpingScores) {
            lerpTimer += delta;

            float progress = Math.min(lerpTimer / lerpDuration, 1f);
            updateLerping(lerpTargetNumber1, lerpStart1, lerpTarget1, progress);
            updateLerping(lerpTargetNumber2, lerpStart2, lerpTarget2, progress);
            if (progress >= 1f) {
                lerpingScores = false;
            }
        }
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = app.getBatch();
        batch.setProjectionMatrix(app.getViewport().getCamera().combined);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        update(delta);

        playerText.draw(delta);
        playerHealth.draw(delta);
        playerScore.draw(delta);

        enemyText.draw(delta);
        enemyHealth.draw(delta);
        enemyScore.draw(delta);

        batch.begin();
        Pencil.I().draw(batch);
        batch.end();
    }

    private void startAnimation() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                moveTo(new Vector2(2, 14), new Vector2(2, 6), 1);
            }
        }, 1.25f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                moveTo(new Vector2(2, 10.5f), new Vector2(2, 9.5f), 0.25f);
            }
        }, 2.25f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                screenShake.addTrauma(1);

                playerScoreIsHigher = playerScore.getValue() > enemyScore.getValue();
                DigitalNumber higher = playerScoreIsHigher ? playerScore : enemyScore;
                DigitalNumber lower = playerScoreIsHigher ? enemyScore : playerScore;

                lower.setValue(lower.getValue() - higher.getValue());
                higher.setValue(0);

                lower.setColor(Assets.I().healthRedColor());
            }
        }, 2.4f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                moveTo(new Vector2(2f, 12.5f), new Vector2(2f, 7.5f), 0.5f);
            }
        }, 2.5f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                DigitalNumber score = playerScoreIsHigher ? enemyScore : playerScore;
                DigitalNumber health = playerScoreIsHigher ? enemyHealth : playerHealth;
                lerpTo(score, health, 0, health.getValue() + score.getValue(), 0.25f);
            }
        }, 3.25f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                ScreenManager.I().setScreen(SlotScreen.class);
            }
        }, 4.25f);
    }

    private void moveTo(Vector2 playerScoreTargetPos, Vector2 enemyScoreTargetPos, float duration) {
        movingScores = true;
        moveTimer = 0f;
        moveDuration = duration;

        playerScoreStart.set(2f, playerScore.getFirstDigitBounds().y);
        enemyScoreStart.set(2f, enemyScore.getFirstDigitBounds().y);

        // Both crash into the middle
        playerScoreTarget.set(playerScoreTargetPos);
        enemyScoreTarget.set(enemyScoreTargetPos);
    }

    private void updateMove(
        DigitalNumber target,
        Vector2 from,
        Vector2 to,
        float progress
    ) {
        float eased = Interpolation.pow2Out.apply(progress);

        float x = from.x + (to.x - from.x) * eased;
        float y = from.y + (to.y - from.y) * eased;

        target.getFirstDigitBounds().x = x;
        target.getFirstDigitBounds().y = y;
    }

    private void lerpTo(DigitalNumber target1, DigitalNumber target2, float scoreTarget1, float scoreTarget2, float duration) {
        lerpingScores = true;
        lerpTimer = 0f;
        lerpDuration = duration;

        lerpTargetNumber1 = target1;
        lerpTargetNumber2 = target2;

        lerpStart1 = target1.getValue();
        lerpStart2 = target2.getValue();

        lerpTarget1 = scoreTarget1;
        lerpTarget2 = scoreTarget2;
    }

    private void updateLerping(DigitalNumber target, float from, float to, float progress) {
        float eased = Interpolation.smooth.apply(progress);
        float value = from + (to - from) * eased;

        target.setValue(value);
    }
}
