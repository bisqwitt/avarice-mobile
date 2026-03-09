package com.avaricious.components;

import com.avaricious.components.slot.SlotMachine;
import com.avaricious.effects.GlowBorder;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CardDestinationUI {

    private final TextureRegion yellowTexture = Assets.I().get(AssetKey.YELLOW_PIXEL);
    private final TextureRegion redTexture = Assets.I().get(AssetKey.DARK_RED_PIXEL);

    public void draw(SpriteBatch batch, float delta, Vector2 cardCenterPos, boolean cardIsDragging) {

        Rectangle window = SlotMachine.windowBounds;
        Rectangle deckCardBounds = DeckUi.I().getFirstCardBounds();

        if (cardIsDragging) {
            GlowBorder.drawGlowBorder(yellowTexture, window, window.contains(cardCenterPos), ZIndex.GLOW_BORDER_CARD_DESTINATION, delta);
//            GlowBorder.drawGlowBorder(redTexture, deckHitBox, deckHitBox.contains(cardCenterPos), ZIndex.GLOW_BORDER_CARD_DESTINATION, delta);
        }
    }
}
