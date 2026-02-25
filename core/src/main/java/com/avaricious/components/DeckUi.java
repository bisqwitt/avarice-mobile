package com.avaricious.components;

import com.avaricious.cards.Card;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.upgrades.Deck;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeckUi {

    private static DeckUi instance;

    public static DeckUi I() {
        return instance == null ? instance = new DeckUi() : instance;
    }

    private DeckUi() {
        Deck.I().onChange(newDeck -> pendingCards = newDeck);
    }

    private final float CARD_WIDTH = 142 / 85f;
    private final float CARD_HEIGHT = 190 / 85f;
    private final Rectangle firstCardBounds = new Rectangle(7f, 0.25f, CARD_WIDTH, CARD_HEIGHT);

    private final TextureRegion jokerCard = Assets.I().get(AssetKey.JOKER_CARD);
    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final Map<Card, DragableSlot> cards = new LinkedHashMap<>();

    private final Vector2 touchDownLocation = new Vector2();

    private List<? extends Card> pendingCards;

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        if (pendingCards != null) {
            loadPendingCards();
        }

        if (pressed && !wasPressed) {
            touchDownLocation.set(mouse.x, mouse.y);
        }

        if (!pressed && wasPressed) {
            if (firstCardBounds.contains(touchDownLocation) && firstCardBounds.contains(mouse)) {
                Pencil.I().toggleDarkenEverythingBehindWindow();
            }
        }
    }

    public void draw(SpriteBatch batch) {
        Pencil.I().drawInColor(batch, Assets.I().shadowColor(),
            () -> batch.draw(jokerCardShadow, 6.8f, 0.05f, CARD_WIDTH + 0.4f, CARD_HEIGHT + 0.4f));
        for (DragableSlot card : cards.values()) {
            Vector2 pos = card.getRenderPos(new Vector2());
            batch.draw(jokerCard,
                pos.x, pos.y,
                firstCardBounds.width, firstCardBounds.height);
        }
    }

    private void loadPendingCards() {
        cards.clear();
        for (int i = 0; i < pendingCards.size(); i++) {
            Rectangle bounds = new Rectangle(
                firstCardBounds.x + 0.025f * i, firstCardBounds.y + 0.025f * i,
                firstCardBounds.width, firstCardBounds.height
            );
            cards.put(pendingCards.get(i), new DragableSlot(bounds).setTilt(200f, 20f));
        }
    }

    public Vector2 getTopCardSpawnPos() {
        return new Vector2(firstCardBounds.x, firstCardBounds.y);
    }

    public Vector2 getTopCardCenter(Vector2 out) {
        return out.set(
            firstCardBounds.x + firstCardBounds.width / 2f,
            firstCardBounds.y + firstCardBounds.height / 2f
        );
    }

}
