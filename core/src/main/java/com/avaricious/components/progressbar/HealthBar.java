package com.avaricious.components.progressbar;

import com.avaricious.AssetKey;
import com.avaricious.Assets;
import com.avaricious.DevTools;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.EvadeChance;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class HealthBar extends ProgressBar {

    private final float X = 1.35f;
    private final float Y = 5.3f;
    private final float CELL_OFFSET = 0.04325f;

    private final TextureRegion heart = Assets.I().get(AssetKey.HEART);
    private final TextureRegion lightGreyPixel = Assets.I().get(AssetKey.LIGHT_GREY_PIXEL);

    private float currentValue;

    public HealthBar(float maxHealth) {
        super(165, Assets.I().get(AssetKey.HEALTH_RED_PIXEL));
        setMaxValue(maxHealth);
    }

    public void setCurrentHealth(float currentHealth) {
        currentValue = MathUtils.clamp(currentHealth, 0f, getMaxValue());
    }

    @Override
    public void draw(SpriteBatch batch) {
        float diff = Math.abs(currentValue - getDisplayedValue());
        float nextStep = diff / 30 < 5 ? 5 : diff / 30;
        setDisplayedValue(diff < 1 ? currentValue : currentValue > getDisplayedValue()
            ? getDisplayedValue() + diff / 30 : getDisplayedValue() - diff / 30);

//        batch.setColor(1f, 1f, 1f, 0.25f);
//        batch.draw(box, 12.95f, 1.2f, 1.5f, 5.4f);
//        batch.setColor(1f, 1f, 1f, 1f);

        batch.draw(lightGreyPixel, X - 1.2f, Y - 0.25f, 8.7f, 0.75f);

        for (int i = 0; i < progress.length; i++) {
            batch.draw(progress[i], X + (i * CELL_OFFSET), Y, 2 / 22f, 6 / 22f);
        }
//        batch.draw(border, 14.8f, 3.7f, 14 / 70f, 310 / 70f);
        batch.draw(heart, X - 1f, Y - 0.15f, 12 / 20f, 11 / 20f);
    }

    public float getMaxHealth() {
        return getMaxValue();
    }

    public float getCurrentHealth() {
        return currentValue;
    }

    public void damage(float amount) {
        if (DevTools.noDamage) return;
        EvadeChance evadeStatus = PlayerStats.I().getStat(EvadeChance.class);

        if (evadeStatus.rollChance()) {
            PopupManager.I().spawnStatisticHit(evadeStatus.getTexture(), X + (getFurthestCellIndex() * CELL_OFFSET), Y + 0.5f);
        } else {
            setCurrentHealth(currentValue - amount);
        }
    }

    public void heal(float amount) {
        setCurrentHealth(currentValue + amount);
    }
}
