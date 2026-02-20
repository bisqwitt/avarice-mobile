package com.avaricious.components.progressbar;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.screens.ScreenManager;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.EvadeChance;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Health {

    private static Health instance;

    public static Health I() {
        return instance == null ? instance = new Health() : instance;
    }

    private final float Y = 0.5f;
    private final ArmorBar armorBar = new ArmorBar(100f, Y + 1f);
    private final HealthBar healthBar = new HealthBar(100f, Y);

    private Health() {
        armorBar.setCurrentHealth(0);
        healthBar.setCurrentHealth(healthBar.getMaxHealth());
    }

    public void draw(SpriteBatch batch) {
        armorBar.draw(batch);
        healthBar.draw(batch);
    }

    public void damage(float damage) {
        EvadeChance evadeChanceStatus = PlayerStats.I().getStat(EvadeChance.class);
        if (evadeChanceStatus.rollChance()) {
            HealthBar barToDamage = armorBar.getCurrentHealth() > 0 ? armorBar : healthBar;
            PopupManager.I().spawnStatisticHit(
                evadeChanceStatus.getTexture(),
                barToDamage.X + (barToDamage.getFurthestCellIndex() * barToDamage.CELL_OFFSET),
                barToDamage.Y + 0.5f);
            return;
        }

        float armorHp = armorBar.getCurrentHealth();
        if (armorHp > 0) {
            float armorDamage = Math.min(damage, armorHp);
            float spill = damage - armorDamage;

            armorBar.damage(damage);
            if (spill > 0) {
                healthBar.damage(spill);
                PatternDisplay.I().reset();
            }
        } else {
            healthBar.damage(damage);
            PatternDisplay.I().reset();
        }

        if (healthBar.getCurrentHealth() <= 0) {
            ScreenManager.restartGame();
        }
    }

    public ArmorBar getArmorBar() {
        return armorBar;
    }

    public HealthBar getHealthBar() {
        return healthBar;
    }
}
