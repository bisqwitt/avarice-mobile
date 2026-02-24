package com.avaricious.components;

import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DeckUi {

    private final float CARD_WIDTH = 142 / 85f;
    private final float CARD_HEIGHT = 190 / 85f;

    private final Rectangle firstCardBounds = new Rectangle(7f, 0.25f, CARD_WIDTH, CARD_HEIGHT);

    private final TextureRegion jokerCard = Assets.I().get(AssetKey.JOKER_CARD);
    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final Vector2 touchDownLocation = new Vector2();

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        if (pressed && !wasPressed) {
            touchDownLocation.set(mouse.x, mouse.y);
        }

        if (!pressed && wasPressed) {
            if (firstCardBounds.contains(touchDownLocation) && firstCardBounds.contains(mouse)) {
                Pencil.I().setDarkenEverythingBehindWindow(true);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        Pencil.I().drawInColor(batch, Assets.I().shadowColor(),
            () -> batch.draw(jokerCardShadow, 6.8f, 0.05f, CARD_WIDTH + 0.4f, CARD_HEIGHT + 0.4f));
        for (int i = 0; i < 5; i++) {
            batch.draw(jokerCard,
                firstCardBounds.x + 0.025f * i, firstCardBounds.y + 0.025f * i,
                firstCardBounds.width, firstCardBounds.height);
        }
    }

}
