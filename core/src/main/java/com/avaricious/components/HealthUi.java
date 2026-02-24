package com.avaricious.components;

import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
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

    private final float numberWidth = 7 / 15f;
    private final float numberHeight = 11 / 15f;
    private final float numberOffset = 0.6f;

    private final float healthY = 0.5f;
    private final float armorY = healthY + 1.1f;
    //    private final float txtX = 0.25f;
    private final float currentValueX = 0.35f;
    private final float maxValueX = currentValueX + 2.25f;

    private final DigitalNumber currentArmor;
    private final DigitalNumber maxArmor;

    private final DigitalNumber currentHealth;
    private final DigitalNumber maxHealth;

    private final TextureRegion hpTxt = Assets.I().get(AssetKey.HP_TXT);
    private final TextureRegion hpTxtShadow = Assets.I().get(AssetKey.HP_TXT_SHADOW);
    private final TextureRegion armTxt = Assets.I().get(AssetKey.ARM_TXT);
    private final TextureRegion armTxtShadow = Assets.I().get(AssetKey.ARM_TXT_SHADOW);
    private final TextureRegion slashSymbol = Assets.I().get(AssetKey.SLASH_SYMBOL);

    private HealthUi() {
        currentArmor = new DigitalNumber(0, Assets.I().silver(), 3,
            new Rectangle(currentValueX, armorY, numberWidth, numberHeight), numberOffset);
        maxArmor = new DigitalNumber(100, Assets.I().silver(), 3,
            new Rectangle(maxValueX, armorY, numberWidth, numberHeight), numberOffset);

        currentHealth = new DigitalNumber(100, Assets.I().healthRedColor(), 3,
            new Rectangle(currentValueX, healthY, numberWidth, numberHeight), numberOffset);
        maxHealth = new DigitalNumber(100, Assets.I().healthRedColor(), 3,
            new Rectangle(maxValueX, healthY, numberWidth, numberHeight), numberOffset);
    }

    public void draw(SpriteBatch batch, float delta) {
//        Pencil.I().drawInColor(batch, Assets.I().shadowColor(),
//            () -> {
//                batch.draw(armTxtShadow, txtX, armorY + 0.05f - 0.1f, 31 / 25f, 11 / 25f);
//                batch.draw(hpTxtShadow, txtX + 0.25f, healthY + 0.05f - 0.1f, 18 / 25f, 11 / 25f);
//            });

        currentArmor.draw(batch, delta);
        Pencil.I().drawInColor(batch, Assets.I().silver(),
            () -> {
//                batch.draw(armTxt, txtX, armorY + 0.05f, 31 / 25f, 11 / 25f);
                batch.draw(slashSymbol, currentValueX + 1.75f, armorY, numberWidth, numberHeight);
            });
        maxArmor.draw(batch, delta);

        currentHealth.draw(batch, delta);
        Pencil.I().drawInColor(batch, Assets.I().healthRedColor(),
            () -> {
//                batch.draw(hpTxt, txtX + 0.25f, healthY + 0.05f, 18 / 25f, 11 / 25f);
                batch.draw(slashSymbol, currentValueX + 1.75f, healthY, numberWidth, numberHeight);
            });
        maxHealth.draw(batch, delta);
    }

    public void damage(int damage) {
        float currentArmorValue = currentArmor.getScore();

        EvadeChance evadeChanceStatus = PlayerStats.I().getStat(EvadeChance.class);
        if (evadeChanceStatus.rollChance()) {
            PopupManager.I().spawnStatisticHit(
                evadeChanceStatus.getTexture(),
                currentValueX + 1f,
                (currentArmorValue > 0 ? armorY : healthY) + 0.5f);
            return;
        }

        float armorHp = currentArmor.getScore();
        if (armorHp > 0) {
            float armorDamage = Math.min(damage, armorHp);
            float spill = damage - armorDamage;

            damageArmor(damage);
            if (spill > 0) {
                damageHealth(damage);
                PatternDisplay.I().reset();
            }
        } else {
            damageHealth(damage);
            PatternDisplay.I().reset();
        }

        if (currentHealth.getScore() <= 0) {
            ScreenManager.restartGame();
        }
    }

    public void healHealth() {
        currentHealth.setScore(maxHealth.getScore());
    }

    private void damageArmor(int damage) {
        currentArmor.setScore(currentArmor.getScore() - damage);
    }

    private void damageHealth(int damage) {
        currentHealth.setScore(currentHealth.getScore() - damage);
    }

}
