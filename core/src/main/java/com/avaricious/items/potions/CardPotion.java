package com.avaricious.items.potions;

import com.avaricious.items.upgrades.Hand;
import com.avaricious.items.upgrades.IUpgradeType;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CardPotion extends AbstractPotion {

    private final TextureRegion texture = Assets.I().get(AssetKey.CARD_POTION);
    private final TextureRegion shadowTexture = Assets.I().get(AssetKey.CARD_POTION_SHADOW);

    @Override
    protected void onUse() {
        Hand.I().drawCard();
    }

    @Override
    public String title() {
        return "Card Potion";
    }

    @Override
    public String description() {
        return "Draw a Card";
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
        return 24;
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
