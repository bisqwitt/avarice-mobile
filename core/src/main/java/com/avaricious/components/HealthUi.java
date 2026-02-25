package com.avaricious.components;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.effects.BorderPulseMesh;
import com.avaricious.screens.ScreenManager;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.EvadeChance;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class HealthUi {

    private static HealthUi instance;

    public static HealthUi I() {
        return instance == null ? instance = new HealthUi() : instance;
    }

    private final float hpSizeRatio = 15f;
    private final float armorSizeRatio = 20f;

    private final float healthY = 0.6f;
    private final float armorY = healthY + 1.1f;
    private final float txtX = 0.75f;
    private final float currentValueX = txtX + 1.5f;

    private final DigitalNumber armor;
    private final DigitalNumber health;

    private final TextureRegion hpTxt = Assets.I().get(AssetKey.HP_TXT);
    private final TextureRegion hpTxtShadow = Assets.I().get(AssetKey.HP_TXT_SHADOW);
    private final TextureRegion armTxt = Assets.I().get(AssetKey.ARM_TXT);
    private final TextureRegion armTxtShadow = Assets.I().get(AssetKey.ARM_TXT_SHADOW);

    private HealthUi() {
        armor = new DigitalNumber(0, Assets.I().silver(), 3,
            new Rectangle(currentValueX + 0.35f, armorY, 7 / armorSizeRatio, 11 / armorSizeRatio), 0.45f);

        health = new DigitalNumber(100, Assets.I().healthRedColor(), 3,
            new Rectangle(currentValueX, healthY, 7 / hpSizeRatio, 11 / hpSizeRatio), 0.55f);
    }

    public void draw(SpriteBatch batch, float delta) {
        Pencil.I().drawInColor(batch, Assets.I().shadowColor(),
            () -> {
                batch.draw(armTxtShadow, txtX - 0.15f, armor.calcHoverY() - 0.1f, 31 / armorSizeRatio, 11 / armorSizeRatio);
                batch.draw(hpTxtShadow, txtX, health.calcHoverY() - 0.1f, 18 / hpSizeRatio, 11 / hpSizeRatio);
            });

        armor.draw(batch, delta);
        Pencil.I().drawInColor(batch, Assets.I().silver(),
            () -> batch.draw(armTxt, txtX - 0.15f, armor.calcHoverY(), 31 / armorSizeRatio, 11 / armorSizeRatio));

        health.draw(batch, delta);
        Pencil.I().drawInColor(batch, Assets.I().healthRedColor(),
            () -> batch.draw(hpTxt, txtX, health.calcHoverY(), 18 / hpSizeRatio, 11 / hpSizeRatio));
    }

    public void damage(int damage) {
        float currentArmorValue = armor.getScore();

        EvadeChance evadeChanceStatus = PlayerStats.I().getStat(EvadeChance.class);
        if (evadeChanceStatus.rollChance()) {
            PopupManager.I().spawnStatisticHit(
                evadeChanceStatus.getTexture(),
                currentValueX + 1f,
                (currentArmorValue > 0 ? armorY : healthY) + 0.5f);
            return;
        }

        float armorHp = armor.getScore();
        if (armorHp > 0) {
            float armorDamage = Math.min(damage, armorHp);
            float spill = damage - armorDamage;

            damageArmor(damage);
            if (spill > 0) {
                damageHealth(damage);
                PatternDisplay.I().clearNumbers();
            }
        } else {
            damageHealth(damage);
            PatternDisplay.I().clearNumbers();
        }

        if (health.getScore() <= 0) {
            ScreenManager.restartGame();
        }
    }

    public void healHealth() {
        health.setScore(100);
    }

    private void damageArmor(int damage) {
        armor.setScore(armor.getScore() - damage);
    }

    private void damageHealth(int damage) {
        health.setScore(health.getScore() - damage);
        BorderPulseMesh.I().triggerOnce(BorderPulseMesh.Type.BLOODY);
        ScreenShake.I().addTrauma(0.55f);
    }

}
