package com.avaricious.components;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DeckUi {

    private final float CARD_WIDTH = 142 / 85f;
    private final float CARD_HEIGHT = 190 / 85f;

    private final TextureRegion jokerCard = Assets.I().get(AssetKey.JOKER_CARD);
    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    public void draw(SpriteBatch batch) {
        batch.setColor(Assets.I().shadowColor());
        batch.draw(jokerCardShadow, 6.4f, 0.2f, CARD_WIDTH + 0.2f, CARD_HEIGHT + 0.2f);
        batch.setColor(1f, 1f, 1f, 1f);
        for (int i = 0; i < 5; i++) {
            batch.draw(jokerCard, 6.5f + 0.05f * i, 0.3f + 0.05f * i, CARD_WIDTH, CARD_HEIGHT);
        }
    }

}
