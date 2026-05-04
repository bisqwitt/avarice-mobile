package com.avaricious.items.potions;

import com.avaricious.components.HealthUi;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ShieldPotion extends AbstractPotion {

    private final TextureRegion texture = Assets.I().get(AssetKey.SHIELD_POTION);
    private final TextureRegion shadowTexture = Assets.I().get(AssetKey.SHIELD_POTION_SHADOW);

    @Override
    protected void onUse() {
        HealthUi.I().addArmor(40);
    }

    @Override
    public String title() {
        return "Shield Potion";
    }

    @Override
    public String description() {
        return "Gain 40 Shield";
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
    public float getTextureWidth() {
        return 14;
    }

    @Override
    public float getTextureHeight() {
        return 25;
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
