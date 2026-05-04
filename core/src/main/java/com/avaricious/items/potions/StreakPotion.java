package com.avaricious.items.potions;

import com.avaricious.components.roundInfoPanel.ScoreDisplay;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StreakPotion extends AbstractPotion {

    private final TextureRegion texture = Assets.I().get(AssetKey.STREAK_POTION);
    private final TextureRegion shadowTexture = Assets.I().get(AssetKey.STREAK_POTION_SHADOW);

    @Override
    protected void onUse() {
        ScoreDisplay.I().addPotentialValue(ScoreDisplay.Type.STREAK, 0.25f);
    }

    @Override
    public String title() {
        return "Streak Potion";
    }

    @Override
    public String description() {
        return "Add 0.25 to Streak";
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
        return 16;
    }

    @Override
    public float getTextureHeight() {
        return 51;
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
