package com.avaricious.upgrades.cards;

import com.avaricious.PackOpening;
import com.avaricious.upgrades.Deck;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.Arrays;
import java.util.List;

public class CardPack extends PackOpening {

    private Card resultCard;

    public CardPack(Rectangle buyBounds) {
        super(new Rectangle(4.85f, 8.1f, 142 / 85f, 190 / 85f), buyBounds);
    }

    @Override
    protected TextureRegion getTexture() {
        if (ripped) return resultCard.texture();
        return Assets.I().get(cardAssetKeys().get(currentTextureIndex));
    }

    @Override
    protected TextureRegion getShadowTexture() {
        return Assets.I().get(AssetKey.JOKER_CARD_SHADOW);
    }

    @Override
    protected TextureRegion getWhiteTexture() {
        return Assets.I().get(AssetKey.WHITE_JOKER_CARD);
    }

    @Override
    protected int getTextureAmount() {
        return cardAssetKeys().size();
    }

    @Override
    protected Upgrade getResult() {
        return resultCard = Deck.I().randomUpgrade();
    }

    @Override
    protected float getTooltipYOffset() {
        return 2.85f;
    }

    private List<AssetKey> cardAssetKeys() {
        return Arrays.asList(
            AssetKey.BALL8_CARD,
            AssetKey.BANNER_CARD,
            AssetKey.BLACKBOARD_CARD,
            AssetKey.CEREMONIAL_DAGGER_CARD,
            AssetKey.CREDIT_CARD_CARD,
            AssetKey.DELAYED_GRATIFICATION_CARD,
            AssetKey.DNA_CARD,
            AssetKey.DUSK_CARD,
            AssetKey.MIME_CARD,
            AssetKey.MISPRINT_CARD,
            AssetKey.MYSTIC_SUMMIT_CARD,
            AssetKey.SPACE_JOKER_CARD,
            AssetKey.SUPERNOVA_CARD
        );
    }

}
