package com.avaricious.components;

import com.avaricious.cards.Card;
import com.avaricious.components.buttons.Button;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.upgrades.Deck;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
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
    private List<? extends Card> pendingCards;

    private final Vector2 touchDownLocation = new Vector2();
    private boolean unfolded = false;
    private Card touchingCard = null;

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        update(delta);

        if (pressed && !wasPressed) {
            touchDownLocation.set(mouse.x, mouse.y);
        }

        if (!pressed && wasPressed) {
            if (firstCardBounds.contains(touchDownLocation) && firstCardBounds.contains(mouse)) {
                toggleShowDeck();
            }
        }

        if (unfolded) {
            if (pressed && !wasPressed) {
                for (Map.Entry<Card, DragableSlot> entry : cards.entrySet()) {
                    Card card = entry.getKey();

                    if (entry.getValue().getBounds().contains(mouse)) {
                        touchingCard = card;
                        cards.get(card).targetScale = 1.3f;
                        cards.get(card).beginDrag(mouse.x, mouse.y, 0);

                        PopupMan
                        PopupManager.I().createTooltip(card, cards.get(card).getRenderPos(new Vector2()), ZIndex.UNFOLDED_DECK_CARD);
                    }
                }
            }

            if (pressed && touchingCard != null) {
                DragableSlot touchingSlot = cards.get(touchingCard);
                Vector2 cardRenderPos = touchingSlot.getRenderPos(new Vector2());

                touchingSlot.dragTo(mouse.x, mouse.y, 0);
                PopupManager.I().updateTooltip(
                    new Vector2(cardRenderPos.x - 2f, cardRenderPos.y + 2.85f),
                    true
                );
            }

            if (!pressed && wasPressed && touchingCard != null) {
                DragableSlot dragableSlot = cards.get(touchingCard);
                dragableSlot.endDrag(0);
                cards.get(touchingCard).targetScale = 1f;
                touchingCard = null;
                PopupManager.I().killTooltip();
            }
        }
    }

    private void update(float delta) {
        if (pendingCards != null) {
            loadPendingCards();
            pendingCards = null;
        }
        for (DragableSlot slot : cards.values()) {
            slot.update(delta);
        }
    }

    public void draw() {
        Pencil.I().addDrawing(new TextureDrawing(
            jokerCardShadow,
            new Rectangle(6.8f, 0.05f, CARD_WIDTH + 0.4f, CARD_HEIGHT + 0.4f),
            ZIndex.DECK_UI_BOX, Assets.I().shadowColor()));

//        if(showingDeck) {
//            returnButton.draw();
//        }
        for (Card card : cards.keySet()) {
            if (card != touchingCard) drawCard(card);
        }
        if (touchingCard != null) drawCard(touchingCard);
    }

    public void drawCard(Card card) {
        DragableSlot slot = cards.get(card);
        Vector2 pos = slot.getRenderPos(new Vector2());
        final float scale = slot.pulseScale() * slot.getTargetScale();
        final float rotation = slot.getDragTiltDeg();

        if (unfolded) {
            Pencil.I().addDrawing(new TextureDrawing(
                jokerCardShadow,
                new Rectangle(pos.x, pos.y - 0.2f, firstCardBounds.width, firstCardBounds.height),
                scale, rotation, ZIndex.UNFOLDED_DECK_CARD, Assets.I().shadowColor()
            ));
        }
        Pencil.I().addDrawing(new TextureDrawing(
            card.texture(),
            new Rectangle(pos.x, pos.y, firstCardBounds.width, firstCardBounds.height),
            scale, rotation, unfolded ? ZIndex.UNFOLDED_DECK_CARD : ZIndex.DECK_UI_CARD
        ));
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

    private void toggleShowDeck() {
        Pencil.I().toggleDarkenEverythingBehindWindow(ZIndex.UNFOLDED_DECK_BACKGROUND);
        if (!unfolded) {
            List<Vector2> positions = new ArrayList<>();
            for (int col = 5; col > 0; col--) {
                for (int row = 0; row < 4; row++) {
                    positions.add(new Vector2(0.7f + row * 2f, 1f + col * 2.5f));
                }
            }

            int index = 0;
            for (DragableSlot card : cards.values()) {
                int finalIndex = index;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        card.moveTo(positions.get(finalIndex));
                    }
                }, index * 0.1f);
                index++;
            }
            unfolded = true;
        } else {
            int index = 0;
            for (DragableSlot card : cards.values()) {
                int finalIndex = index;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        card.moveTo(new Vector2(firstCardBounds.x + 0.025f * finalIndex, firstCardBounds.y + 0.025f * finalIndex));
                    }
                }, index * 0.1f);
                index++;
            }
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    unfolded = false;
                }
            }, index * 0.1f);
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

    public Rectangle getFirstCardBounds() {
        return firstCardBounds;
    }
}
