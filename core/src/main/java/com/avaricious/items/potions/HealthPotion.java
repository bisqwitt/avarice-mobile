package com.avaricious.items.potions;

import com.avaricious.components.HealthUi;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HealthPotion extends AbstractPotion {

    private final TextureRegion texture = Assets.I().get(AssetKey.HEALTH_POTION);
    private final TextureRegion shadowTexture = Assets.I().get(AssetKey.HEALTH_POTION_SHADOW);

    @Override
    public String title() {
        return "Health Potion";
    }

    @Override
    public String description() {
        return "Heal 40 HP";
    }

    @Override
    public int price() {
        return 4;
    }

    @Override
    public TextureRegion texture() {
        return texture;
    }

    @Override
    public TextureRegion shadowTexture() {
        return shadowTexture;
    }

    @Override
    public void onUse() {
        HealthUi.I().healFor(40);
    }

    @Override
    public float getTextureWidth() {
        return 18;
    }

    @Override
    public float getTextureHeight() {
        return 35;
    }

    @Override
    public float getTooltipYOffset() {
        return 2.6f;
    }

    @Override
    public IUpgradeType type() {
        return PotionType.DEFAULT;
    }
}
