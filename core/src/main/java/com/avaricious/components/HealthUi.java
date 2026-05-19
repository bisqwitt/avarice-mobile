package com.avaricious.components;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.components.texts.ArmorText;
import com.avaricious.components.texts.HealthText;
import com.avaricious.effects.BorderPulseMesh;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
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
import com.badlogic.gdx.math.Vector2;

public class HealthUi extends Observable<HealthState> {

    private static HealthUi instance;

    public static HealthUi I() {
        return instance == null ? instance = new HealthUi() : instance;
    }

    private final DigitalNumber armor;
    private final DigitalNumber health;

    private final HealthText healthText = new HealthText(new Vector2(0.475f, 19.1f), 30f, 0.05f, ZIndex.HEALTH_UI);
    private final ArmorText armorText = new ArmorText(new Vector2(2.5f, 19.1f), 30f, 0.05f, ZIndex.HEALTH_UI);

    private HealthUi() {
        armor = new DigitalNumber(10, Assets.I().silver(), 2,
            new Rectangle(3f, 18.35f, 7 / 23f, 11 / 23f), 0.4f);

        health = new DigitalNumber(1000, Assets.I().healthRedColor(), 4,
            new Rectangle(0.65f, 18.35f, 7 / 23f, 11 / 23f), 0.4f);
        health.getScaleEffect().setStrength(0.08f, 1.25f);
        health.getScaleEffect().setAllowed(true);
        health.getScaleEffect().setEnabled(false);
    }

    public void draw(float delta) {
//        healthText.draw(delta);
//        health.draw(delta);
        armorText.draw(delta);
        armor.draw(delta);
    }

    public boolean damage(int damage) {
        boolean damagedHealth = false;
        float currentArmorValue = armor.getValue();

        EvadeChance evadeChanceStatus = PlayerStats.I().getStat(EvadeChance.class);
//        if (evadeChanceStatus.rollChance()) {
//            PopupManager.I().spawnStatisticHit(
//                evadeChanceStatus.getTexture(),
//                currentValueX + 1f,
//                (currentArmorValue > 0 ? armorY : healthY) + 0.5f);
//            return false;
//        }

        float armorHp = armor.getValue();
        if (armorHp > 0) {
            float armorDamage = Math.min(damage, armorHp);
            float spill = damage - armorDamage;

            damageArmor((int) armorDamage);
            if (spill > 0) {
                damageHealth((int) spill);
                damagedHealth = true;
            }
        } else {
            damageHealth(damage);
            damagedHealth = true;
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
        ScreenManager.I().getScreen(SlotScreen.class).onCashoutButtonPressed();
//        setHealth((int) health.getValue() - damage);
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
