package com.avaricious.upgrades.cards;

import com.avaricious.components.HealthUi;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.upgrades.IUpgradeWithActionOnSpinButtonPressed;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HealForEveryFruitHitCard extends AbstractCard implements IUpgradeWithActionOnSpinButtonPressed {

    private final TextureRegion texture = Assets.I().get(AssetKey.GROS_MICHEL);
    private int fruitsHit = 0;

    @Override
    public String description() {
        return "Heal " + Assets.I().healthRedText("1 HP") + " for every fruit hit last spin\n"
            + "(" + fruitsHit + ")";
    }

    @Override
    protected void onApply() {
        HealthUi.I().healFor(fruitsHit);
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public Runnable createPopupRunnable(Vector2 pos) {
        return () -> PopupManager.I().spawnNumber(createNumberPopup(fruitsHit, pos, Assets.I().healthRedColor()));
    }

    public void onFruitHit() {
        fruitsHit++;
    }

    @Override
    public void onSpinButtonPressed() {
        fruitsHit = 0;
    }
}
