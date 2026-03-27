package com.avaricious.components;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.effects.BorderPulseMesh;
import com.avaricious.screens.ScreenManager;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.EvadeChance;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class HealthUi {

    private static HealthUi instance;

    public static HealthUi I() {
        return instance == null ? instance = new HealthUi() : instance;
    }

    private final float hpSizeRatio = 20f;
    private final float armorSizeRatio = 20f;

    private final float healthY = 18.8f;
    private final float armorY = healthY;
    private final float txtX = 0.75f;
    private final float currentValueX = txtX + 1.25f;

    private final DigitalNumber armor;
    private final DigitalNumber health;

    private final TextureRegion hpTxt = Assets.I().get(AssetKey.HP_TXT);
    private final TextureRegion hpTxtShadow = Assets.I().get(AssetKey.HP_TXT_SHADOW);
    private final TextureRegion armTxt = Assets.I().get(AssetKey.DF_TXT);
    private final TextureRegion armTxtShadow = Assets.I().get(AssetKey.ARM_TXT_SHADOW);

    private HealthUi() {
        armor = new DigitalNumber(0, Assets.I().silver(), 3,
            new Rectangle(currentValueX + 5.1f, armorY, 7 / armorSizeRatio, 11 / armorSizeRatio), 0.4f);

        health = new DigitalNumber(100, Assets.I().healthRedColor(), 3,
            new Rectangle(currentValueX, healthY, 7 / hpSizeRatio, 11 / hpSizeRatio), 0.4f);
    }

    public void draw(float delta) {
//
//        Pencil.I().addDrawing(new TextureDrawing(
//            armTxtShadow,
//            new Rectangle(txtX + 6, armor.calcNumberY() - 0.1f, 31 / armorSizeRatio, 11 / armorSizeRatio),
//            ZIndex.HEALTH_UI, Assets.I().shadowColor()
//        ));
        Pencil.I().addDrawing(new TextureDrawing(
            hpTxtShadow,
            new Rectangle(txtX, health.calcNumberY() - 0.1f, 18 / hpSizeRatio, 11 / hpSizeRatio),
            ZIndex.HEALTH_UI, Assets.I().shadowColor()
        ));

        armor.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(armTxt,
            new Rectangle(txtX + 5.1f, armor.calcNumberY(), 18 / armorSizeRatio, 11 / armorSizeRatio),
            ZIndex.HEALTH_UI, Assets.I().silver()));

        health.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            hpTxt,
            new Rectangle(txtX, health.calcNumberY(), 18 / hpSizeRatio, 11 / hpSizeRatio),
            ZIndex.HEALTH_UI, Assets.I().healthRedColor()
        ));
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

            damageArmor((int) armorDamage);
            if (spill > 0) {
                damageHealth((int) spill);
                ScoreDisplay.I().clearPotentialScore();
            }
        } else {
            damageHealth(damage);
            ScoreDisplay.I().clearPotentialScore();
        }

        if (health.getScore() <= 0) {
            ScreenManager.restartGame();
        }
    }

    public void healHealth() {
        setHealth(100);
    }

    public void healFor(int amount) {
        setHealth(health.getScore() + amount);
    }

    public void addArmor(int amount) {
        setArmor(armor.getScore() + amount);
    }

    public int getHealth() {
        return health.getScore();
    }

    public int getArmor() {
        return armor.getScore();
    }

    private void damageArmor(int damage) {
        setArmor(armor.getScore() - damage);
    }

    private void damageHealth(int damage) {
        setHealth(health.getScore() - damage);
        BorderPulseMesh.I().triggerOnce(BorderPulseMesh.Type.BLOODY);
        ScreenShake.I().addTrauma(0.55f);
    }

    private void setHealth(int value) {
        health.setScore(value);
    }

    private void setArmor(int value) {
        armor.setScore(value);
    }

}
