package com.avaricious.components;

import com.avaricious.cards.Card;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Hand;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandUi {

    private final CardDestinationUI cardDestinationUI = new CardDestinationUI();

    private final float Y = 3f;
    private final float CARD_OFFSET = 1.75f;
    private final float CARD_WIDTH = 142 / 90f;
    private final float CARD_HEIGHT = 190 / 90f;

    private final TextureRegion jokerCard = Assets.I().get(AssetKey.JOKER_CARD);

    private final Map<Card, DragableSlot> cards = new HashMap<>();

    private Vector2 touchDownLocation = new Vector2();
    private boolean movedSinceTouchDown = false;

    private Card touchingCard = null;
    private Card selectedCard = null;
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
            float dragThreshHold = 0.5f; // How much to drag card to count as dragging and not selecting
            if (!movedSinceTouchDown && touchDownLocation.dst2(mouse) > dragThreshHold * dragThreshHold) {
                movedSinceTouchDown = true;
            }
            cards.get(touchingCard).dragTo(mouse.x, mouse.y, 0);
        }

        if (!pressed && wasPressed && touchingCard != null) {
            onCardTouchReleased(touchingCard);
        }
    }

    private void onCardTouchDown(Card card, Vector2 mouse) {
        touchDownLocation.set(mouse);
        movedSinceTouchDown = false;

        touchingCard = card;
        cards.get(card).targetScale = 1.3f;
        cards.get(card).beginDrag(mouse.x, mouse.y, 0);
    }

    private void onCardTouchReleased(Card card) {
        if (movedSinceTouchDown) {
            DragableSlot dragableSlot = cards.get(card);
            if (SlotMachine.windowBounds.contains(dragableSlot.getCardCenter())) {
                applyCard(card);
            } else if (cardDestinationUI.isOverDumpster(dragableSlot.getCardCenter())) {
                discardCard(card);
            }
            dragableSlot.endDrag(0);
            cards.get(card).targetScale = 1f;
        } else if (selectedCard == null) {
            selectedCard = touchingCard;
        } else {
            selectedCard = null;
            cards.get(card).targetScale = 1f;
        }
        touchingCard = null;
    }

    public void draw(SpriteBatch batch, float delta) {
        for (DragableSlot slot : cards.values()) slot.update(delta);

        Vector2 touchingCardCenter = touchingCard != null ? cards.get(touchingCard).getCardCenter() : new Vector2();
        cardDestinationUI.draw(batch, delta, touchingCardCenter, touchingCard != null);

        for (Card card : cards.keySet()) {
            drawCard(batch, card);
        }
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

        if (selectedCard == card)
            PopupManager.I().renderTooltip(selectedCard, position.x - 2f, position.y + 2.65f);

        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(jokerCard,
            position.x, position.y,
            bounds.width / 2f, bounds.height / 2f,
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
        int n = cards.size();
        if (n == 0) return;

        float screenWidth = ScreenManager.getViewport().getWorldWidth();
        float handWidth = (n - 1) * CARD_OFFSET + CARD_WIDTH;
        float firstX = (screenWidth - handWidth) / 2f;

        int i = 0;
        for (DragableSlot slot : cards.values()) {
            slot.getBounds().x = firstX + i * CARD_OFFSET;
            i++;
        }
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
