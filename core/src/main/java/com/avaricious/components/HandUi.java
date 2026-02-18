package com.avaricious.components;

import com.avaricious.cards.Card;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Hand;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HandUi {

    private final CardDestinationUI cardDestinationUI = new CardDestinationUI();

    private final float Y = 3f;
    private final float CARD_OFFSET = 1.75f;
    private final float CARD_WIDTH = 142 / 90f;
    private final float CARD_HEIGHT = 190 / 90f;

    private final TextureRegion jokerCard = Assets.I().get(AssetKey.JOKER_CARD);
    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final Map<Card, DragableSlot> cards = new HashMap<>();
    private final Map<Card, Integer> cardIndexes = new HashMap<>();

    private Vector2 touchDownLocation = new Vector2();
    private boolean cardMovedSinceTouchDown = false;
    private boolean cardInTooltipRange = false;

    private Card touchingCard = null;
    private Card discardingCard = null;



    private List<? extends Card> pendingHand;

    public HandUi() {
        loadCards(Hand.I().getHand());
        Hand.I().onChange(newHand -> pendingHand = newHand);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        if (pendingHand != null) {
            loadCards(pendingHand);
            pendingHand = null;
        }

        if (pressed && !wasPressed) {
            for (Map.Entry<Card, DragableSlot> entry : cards.entrySet()) {
                if (entry.getValue().getBounds().contains(mouse))
                    onCardTouchDown(entry.getKey(), mouse);
            }
        }

        if (pressed && touchingCard != null) {
            float dragThreshHold = 0.12f; // How much to drag card to count as dragging and not selecting
            if (!cardMovedSinceTouchDown && touchDownLocation.dst2(mouse) > dragThreshHold * dragThreshHold) {
                cardMovedSinceTouchDown = true;
            }
            cards.get(touchingCard).dragTo(mouse.x, mouse.y, 0);
            cardInTooltipRange = new Rectangle(0f, 0f, 9f, 8f).contains(mouse);
        }

        if (!pressed && wasPressed && touchingCard != null) {
            onCardTouchReleased(touchingCard);
        }
    }

    private void onCardTouchDown(Card card, Vector2 mouse) {
        touchDownLocation.set(mouse);
        cardMovedSinceTouchDown = false;

        touchingCard = card;
        cards.get(card).targetScale = 1.3f;
        cards.get(card).beginDrag(mouse.x, mouse.y, 0);
    }

    private void onCardTouchReleased(Card card) {
        if (cardMovedSinceTouchDown) {
            DragableSlot dragableSlot = cards.get(card);
            if (SlotMachine.windowBounds.contains(dragableSlot.getCardCenter())) {
                applyCard(card);
            } else if (cardDestinationUI.isOverDumpster(dragableSlot.getCardCenter())) {
                discardCard(card);
            }
            dragableSlot.endDrag(0);
        }
        touchingCard = null;
        cards.get(card).targetScale = 1f;
    }

    public void draw(SpriteBatch batch, float delta) {
        for (DragableSlot slot : cards.values()) slot.update(delta);

        Vector2 touchingCardCenter = touchingCard != null ? cards.get(touchingCard).getCardCenter() : new Vector2();
        cardDestinationUI.draw(batch, delta, touchingCardCenter, touchingCard != null);

        for (Card card : cards.keySet()) {
            if(touchingCard != card) drawCard(batch, card);
        }

        if(touchingCard != null) drawCard(batch, touchingCard);
    }

    private void drawCard(SpriteBatch batch, Card card) {
        Rectangle bounds = cards.get(card).getBounds();
        DragableSlot slot = cards.get(card);

        float scale = slot.pulseScale()
            * slot.wobbleScale()
            * slot.getTargetScale();
        float rotation = slot.wobbleAngleDeg() + slot.getDragTiltDeg();
        float alpha = slot.getAlpha();

        Vector2 position = slot.getRenderPos(new Vector2());
        if (discardingCard == card)
            position.x += cardDestinationUI.getDumpster().getCurrentSlideValue();

        float originX = bounds.width / 2f;
        float originY = bounds.width / 2f;

        Color shadowColor = Assets.I().shadowColor();
        batch.setColor(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(0.25f, alpha));
        batch.draw(jokerCardShadow,
            position.x, position.y - (card == touchingCard ? 0.2f : 0.1f),
            originX, originY,
            bounds.width, bounds.height,
            scale, scale,
            rotation
            );

        if (touchingCard == card && cardInTooltipRange)
            PopupManager.I().renderTooltip(touchingCard, position.x - 2f, position.y + 2.65f);

        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(jokerCard,
            position.x, position.y,
            originX, originY,
            bounds.width, bounds.height,
            scale, scale,
            rotation);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void loadCards(List<? extends Card> newHand) {
        // Add new Cards
        for (Card card : newHand) {
            if (!cards.containsKey(card)) {
                Rectangle initialBounds = new Rectangle(
                    (CARD_OFFSET * cards.size()), Y,
                    CARD_WIDTH, CARD_HEIGHT);

                cards.put(card, new DragableSlot(initialBounds));
            }
        }

        // Remove old Cards
        for (Card card : cards.keySet()) {
            if (!newHand.contains(card)) {
                cards.remove(card);
            }
        }

        updateCardBounds();
    }

    private void updateCardBounds() {
        int i = 0;
        float firstX = calcFistX();

        for(Map.Entry<Card, DragableSlot> entry : cards.entrySet()) {
            entry.getValue().getBounds().x = firstX + calcCardIndex(entry.getKey()) * CARD_OFFSET;
            i++;
        }
    }

    private float calcFistX() {
        int n = cards.size();
        if (n == 0) return 0;

        float screenWidth = ScreenManager.getViewport().getWorldWidth();
        float handWidth = (n - 1) * CARD_OFFSET + CARD_WIDTH;
        return (screenWidth - handWidth) / 2f;
    }

    private int calcCardIndex(Card card) {
        List<Map.Entry<Card, DragableSlot>> sorted = new ArrayList<>(cards.entrySet());
        sorted.sort(Comparator.comparingDouble(entry -> entry.getValue().getRenderPos(new Vector2()).x));

        for(Map.Entry<Card, DragableSlot> entry : sorted) {
            if(entry.getKey() == card) return sorted.indexOf(entry);
        }
        return 0;
    }

    public void applyCard(Card card) {
        card.apply();

        DragableSlot slot = cards.get(card);
        slot.pulse();
        slot.wobble();
        Vector2 pos = slot.getRenderPos(new Vector2());
        pos.x += 1.3f;
        pos.y += 1.8f;
        card.createPopupRunnable(pos).run();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                slot.startApplyAnimation(0.6f, () -> Hand.I().removeCardFromHand(card));
            }
        }, 0.5f);
    }

    public void discardCard(Card card) {
        discardingCard = card;
        cards.get(card).startApplyAnimation(0.6f, () -> {
            discardingCard = null;
            Hand.I().removeCardFromHand(card);
        });
    }

}
