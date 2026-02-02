package com.avaricious.components;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.avaricious.components.bars.StatUpgradeBar;
import com.avaricious.components.bars.UpgradeBar;
import com.avaricious.stats.statupgrades.StatUpgrade;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.List;

public class StatusUpgradeWindow {

    private final float WINDOW_X = 1f;
    private final float WINDOW_Y = 6f;

    private final TextureRegion window = Assets.I().get(AssetKey.STATUS_UPGRADE_WINDOW);
    private final TextureRegion shadow = Assets.I().get(AssetKey.STATUS_UPGRADE_WINDOW_SHADOW);
    private final TextureRegion levelUpTxt = new TextureRegion(Assets.I().get(AssetKey.LEVEL_UP_TEXT));
    private boolean show = false;

    private static final float PULSE_SPEED = 3.5f;
    private static final float PULSE_AMPLITUDE = 0.05f;
    private float levelUpAnimTime = 0f;
    private float levelUpTxtScale = 1;

    private final UpgradeBar upgradeBar = new StatUpgradeBar(randomStatUpgrades(),
        new Rectangle(WINDOW_X + 1.5f, WINDOW_Y + 1.6f, 1.25f, 1.25f));

    public StatusUpgradeWindow(Runnable onExit) {
        upgradeBar.setOnUpgradeClickedAndAnimationEnded(() -> {
            show = false;
            onExit.run();
        });
    }

    private List<StatUpgrade> randomStatUpgrades() {
        return Arrays.asList(StatUpgrade.newRandom(), StatUpgrade.newRandom(), StatUpgrade.newRandom());
    }

    public void draw(SpriteBatch batch, float delta) {
        if (!show) return;

        levelUpAnimTime += delta;
        levelUpTxtScale = 1f + (float) Math.sin(levelUpAnimTime * PULSE_SPEED) * PULSE_AMPLITUDE;

//        batch.setColor(1f, 1f, 1f, 0.25f);
//        batch.draw(shadow, 4.1f, 0.85f, 225f / 30f, 163f / 30f);
//        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(window, WINDOW_X, WINDOW_Y, 225f / 30f, 163f / 30f);
        upgradeBar.draw(batch);

        float w = 55f / 15f;
        float h = 13f / 15f;

        batch.draw(
            levelUpTxt,
            WINDOW_X + 1.8f, WINDOW_Y + 3.6f,
            w / 2f, h / 2f,
            w, h,
            levelUpTxtScale, levelUpTxtScale,
            0f
        );
    }

    public void handleInput(Vector2 mouse, boolean leftClickPressed, boolean leftClickWasPressed, float delta) {
        if (!show) return;
        upgradeBar.handleInput(mouse, leftClickPressed, leftClickWasPressed, delta);
    }

    public void show() {
        upgradeBar.loadUpgrades(randomStatUpgrades());
        show = true;
    }

}
