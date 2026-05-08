package com.avaricious.components;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.effects.BorderPulseMesh;
import com.avaricious.screens.ScreenManager;
import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.EvadeChance;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Observable;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class HealthUi extends Observable<HealthState> {

    private static HealthUi instance;

    public static HealthUi I() {
        return instance == null ? instance = new HealthUi() : instance;
    }

    private final float hpSizeRatio = 20f;
    private final float armorSizeRatio = 21f;

    private final float healthY = 18.2f;
    private final float armorY = healthY + 0.75f;
    private final float txtX = 0.75f;
    private final float currentValueX = txtX + 1.25f;

    private final DigitalNumber armor;
    private final DigitalNumber health;

    private final TextureRegion hpTxt = Assets.I().get(AssetKey.HP_TXT);
    private final TextureRegion hpTxtShadow = Assets.I().get(AssetKey.HP_TXT_SHADOW);
    private final TextureRegion armTxt = Assets.I().get(AssetKey.DF_TXT);
    private final TextureRegion armTxtShadow = Assets.I().get(AssetKey.DF_TXT_SHADOW);

    private HealthUi() {
        armor = new DigitalNumber(0, Assets.I().silver(), 3,
            new Rectangle(currentValueX, armorY, 7 / armorSizeRatio, 11 / armorSizeRatio), 0.4f);

        health = new DigitalNumber(100, Assets.I().healthRedColor(), 3,
            new Rectangle(currentValueX, healthY, 7 / hpSizeRatio, 11 / hpSizeRatio), 0.4f);
        health.getScaleEffect().setStrength(0.08f, 1.25f);
        health.getScaleEffect().setAllowed(true);
        health.getScaleEffect().setEnabled(false);
    }

    public void draw(float delta) {
        armor.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            armTxtShadow,
            txtX, armor.calcNumberY() - 0.1f, 18 / armorSizeRatio, 11 / armorSizeRatio,
            armor.getScale(), armor.getRotation(), ZIndex.HEALTH_UI, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(armTxt,
            txtX, armor.calcNumberY(), 18 / armorSizeRatio, 11 / armorSizeRatio,
            armor.getScale(), armor.getRotation(), ZIndex.HEALTH_UI, Assets.I().silver()));

        health.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(
            hpTxtShadow,
            txtX, health.calcNumberY() - 0.1f, 18 / hpSizeRatio, 11 / hpSizeRatio,
            health.getScale(), health.getRotation(), ZIndex.HEALTH_UI, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            hpTxt,
            txtX, health.calcNumberY(), 18 / hpSizeRatio, 11 / hpSizeRatio,
            health.getScale(), health.getRotation(), ZIndex.HEALTH_UI, Assets.I().healthRedColor()
        ));
    }

    public boolean damage(int damage) {
        boolean damagedHealth = false;
        float currentArmorValue = armor.getValue();

        EvadeChance evadeChanceStatus = PlayerStats.I().getStat(EvadeChance.class);
        if (evadeChanceStatus.rollChance()) {
            PopupManager.I().spawnStatisticHit(
                evadeChanceStatus.getTexture(),
                currentValueX + 1f,
                (currentArmorValue > 0 ? armorY : healthY) + 0.5f);
            return false;
        }

        float armorHp = armor.getValue();
        if (armorHp > 0) {
            float armorDamage = Math.min(damage, armorHp);
            float spill = damage - armorDamage;

            damageArmor((int) armorDamage);
            if (spill > 0) {
                damageHealth((int) spill);
                damagedHealth = true;
                ScoreDisplay.I().clearPotentialScore();
            }
        } else {
            damageHealth(damage);
            damagedHealth = true;
            ScoreDisplay.I().clearPotentialScore();
        }

        if (health.getValue() <= 0) {
            ScreenManager.restartGame();
        }
        return damagedHealth;
    }

    public void healHealth() {
        setHealth(100);
    }

    public void healFor(int amount) {
        setHealth((int) health.getValue() + amount);
    }

    public void addArmor(int amount) {
        setArmor((int) armor.getValue() + amount);
    }

    public int getHealth() {
        return (int) health.getValue();
    }

    public int getArmor() {
        return (int) armor.getValue();
    }

    private void damageArmor(int damage) {
        setArmor((int) armor.getValue() - damage);
    }

    private void damageHealth(int damage) {
        setHealth((int) health.getValue() - damage);
        BorderPulseMesh.I().triggerOnce(BorderPulseMesh.Type.BLOODY);
        ScreenShake.I().addTrauma(0.55f);
    }

    private void setHealth(int value) {
        health.setValue(value);

        health.getScaleEffect().setEnabled(value <= 20);
        notifyChanged(snapshot());
    }

    private void setArmor(int value) {
        armor.setValue(value);
        notifyChanged(snapshot());
    }

    public void setHealthState(HealthState healthState) {
        setHealth(healthState.health);
        setArmor(healthState.armor);
    }

    @Override
    protected HealthState snapshot() {
        return new HealthState(getHealth(), getArmor());
    }
}
