package com.avaricious.components;

import com.avaricious.cards.Card;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.effects.particle.ParticleType;
import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Hand;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.UiUtility;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandUi {

    private final CardDestinationUI cardDestinationUI = new CardDestinationUI();

    private final float Y = 3.75f;
    private final float CARD_OFFSET = 1.25f;
    private final float CARD_WIDTH = 142 / 85f;
    private final float CARD_HEIGHT = 190 / 85f;

    private final TextureRegion jokerCard = Assets.I().get(AssetKey.JOKER_CARD);
    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final Map<Card, DragableSlot> cards = new HashMap<>();
    private final Map<Card, Integer> cardIndexLastRender = new HashMap<>();

    private Vector2 touchDownLocation = new Vector2();
    private boolean cardMovedSinceTouchDown = false;

    private Card touchingCard = null;
    private Card applyingCard = null;
    private Card discardingCard = null;

    private List<? extends Card> pendingHand;

    public HandUi() {
        Hand.I().onChange(newHand -> pendingHand = newHand);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        if (pendingHand != null) {
            loadCards(pendingHand);
            pendingHand = null;
        }

        if (pressed && !wasPressed) {
            List<Map.Entry<Card, DragableSlot>> sorted = getEntriesSortedByX();
            Collections.reverse(sorted);
            for (Map.Entry<Card, DragableSlot> entry : sorted) {
                if (entry.getValue().getBounds().contains(mouse)) {
                    onCardTouchDown(entry.getKey(), mouse);
                    break;
                }
            }
        }

        if (pressed && touchingCard != null) {
            onCardTouching(touchingCard, mouse);
        }

        if (!pressed && wasPressed && touchingCard != null) {
            onCardTouchReleased(touchingCard);
        }
    }

    private void onCardTouchDown(Card card, Vector2 mouse) {
        cards.get(card).getRenderPos(touchDownLocation);
        cardMovedSinceTouchDown = false;

        touchingCard = card;
        cards.get(card).targetScale = 1.3f;
        cards.get(card).beginDrag(mouse.x, mouse.y, 0);

        PopupManager.I().createTooltip(card, cards.get(card).getRenderPos(new Vector2()));
    }

    private void onCardTouching(Card card, Vector2 mouse) {
        DragableSlot touchingSlot = cards.get(card);
        Vector2 cardRenderPos = touchingSlot.getRenderPos(new Vector2());

        if (!cardMovedSinceTouchDown &&
            touchDownLocation.dst2(cardRenderPos) > 0) {
            cardMovedSinceTouchDown = true;
        }
        touchingSlot.dragTo(mouse.x, mouse.y, 0);
        updateCardBounds();

        PopupManager.I().updateTooltip(
            new Vector2(cardRenderPos.x - 2f, cardRenderPos.y + 2.85f),
            new Rectangle(0f, 0f, 9f, 8f).contains(mouse));
    }

    private void onCardTouchReleased(Card card) {
        if (cardMovedSinceTouchDown) {
            DragableSlot dragableSlot = cards.get(card);
            if (SlotMachine.windowBounds.contains(dragableSlot.getCardCenter())) {
                applyCard(card);
            } else if (cardDestinationUI.isOverDumpster(dragableSlot.getCardCenter())) {
                discardCard(card);
            } else {
                dragableSlot.endDrag(0);
                cards.get(card).targetScale = 1f;
            }
        } else {
            cards.get(card).targetScale = 1f;
        }
        touchingCard = null;
        PopupManager.I().killTooltip();
        updateCardBounds();
    }

    public void draw(SpriteBatch batch, float delta) {
        for (DragableSlot slot : cards.values()) slot.update(delta);

        Vector2 touchingCardCenter = touchingCard != null ? cards.get(touchingCard).getCardCenter() : new Vector2();
        cardDestinationUI.draw(batch, delta, touchingCardCenter, touchingCard != null);

        for (Map.Entry<Card, DragableSlot> entry : getEntriesSortedByX()) {
            if (touchingCard != entry.getKey()) drawCard(batch, entry.getKey());
        }

        if (touchingCard != null) drawCard(batch, touchingCard);
    }

    private void drawCard(SpriteBatch batch, Card card) {
        Rectangle bounds = cards.get(card).getBounds();
        DragableSlot slot = cards.get(card);

        float scale = slot.pulseScale()
            * slot.wobbleScale()
            * slot.getTargetScale();
        final float rotation = slot.wobbleAngleDeg() + slot.getDragTiltDeg()
            + (card != touchingCard && card != applyingCard ? getHandRotation(card) : 0);

        float alpha = slot.getAlpha();
        if (cardDestinationUI.isOverDumpster(slot.getCardCenter())) alpha -= 0.5f;

        Vector2 position = slot.getRenderPos(new Vector2());

        float originX = bounds.width / 2f;
        float originY = bounds.height / 2f;

        Color shadowColor = Assets.I().shadowColor();
        Vector2 shadowOffset = UiUtility.calcShadowOffset(slot.getCardCenter());
        Pencil.I().addDrawing(new TextureDrawing(
            jokerCardShadow,
            new Rectangle(
                position.x + shadowOffset.x, position.y - (card == touchingCard ? 0.3f : 0.2f),
                bounds.width, bounds.height
            ),
            scale, rotation,
            10, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(0.25f, alpha))
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            jokerCard,
            new Rectangle(position.x, position.y, bounds.width, bounds.height),
            scale, rotation,
            10, new Color(1f, 1f, 1f, alpha)
        ));
    }

    private void loadCards(List<? extends Card> newHand) {
        // Add new Cards
        for (Card card : newHand) {
            if (!cards.containsKey(card)) {

                // 1) Start at deck position
                Vector2 deckSpawn = DeckUi.I().getTopCardSpawnPos();

                Rectangle initialBounds = new Rectangle(
                    deckSpawn.x, deckSpawn.y,
                    CARD_WIDTH, CARD_HEIGHT
                );

                DragableSlot slot = new DragableSlot(initialBounds)
                    .setTilt(200f, 20f);

                // Optional: make the draw feel juicy
//                slot.targetScale = 1.15f;  // slight pop while flying
                slot.pulse();
                slot.wobble();

                cards.put(card, slot);
            }
        }

        // Remove old Cards
        List<Card> cardsToRemove = new ArrayList<>();
        for (Card card : cards.keySet()) {
            if (!newHand.contains(card)) {
                cardsToRemove.add(card);
            }
        }
        for (Card card : cardsToRemove) {
            cards.remove(card);
            cardIndexLastRender.remove(card);
        }

        updateCardBounds();
    }

    private void updateCardBounds() {
        for (Map.Entry<Card, DragableSlot> entry : cards.entrySet()) {
            Card card = entry.getKey();
            DragableSlot slot = entry.getValue();

            int newCardIndex = calcCardIndex(card);
//            if (cardIndexLastRender.containsKey(card) && newCardIndex != cardIndexLastRender.get(card))
//                slot.wobble();
            cardIndexLastRender.put(card, newCardIndex);

            if (card == touchingCard) continue;
            slot.moveTo(new Vector2(calcCardX(card), Y + getHandYOffset(card)));
        }
    }

    private float calcCardX(Card card) {
        return calcFistX() + calcCardIndex(card) * CARD_OFFSET;
    }

    private float calcFistX() {
        int n = cards.size();
        if (n == 0) return 0;

        float screenWidth = ScreenManager.getViewport().getWorldWidth();
        float handWidth = (n - 1) * CARD_OFFSET + CARD_WIDTH;
        return (screenWidth - handWidth) / 2f;
    }

    private int calcCardIndex(Card card) {
        List<Map.Entry<Card, DragableSlot>> sorted = getEntriesSortedByX();
        for (Map.Entry<Card, DragableSlot> entry : sorted) {
            if (entry.getKey() == card) return sorted.indexOf(entry);
        }
        return 0;
    }

    private List<Map.Entry<Card, DragableSlot>> getEntriesSortedByX() {
        List<Map.Entry<Card, DragableSlot>> sorted = new ArrayList<>(cards.entrySet());
        sorted.sort(Comparator.comparingDouble(entry -> entry.getValue().getRenderPos(new Vector2()).x));
        return sorted;
    }

    private float getHandRotation(Card card) {
        int i = calcCardIndex(card);
        int n = cards.size();
        if (n <= 1) return 0f;

        float t = (i / (float) (n - 1)) * 2f - 1f; // [-1..+1]

        float fanMaxDeg = 6f;
        float jitterMaxDeg = 1.0f;

        float curve = t * t * t;

        int h = card.hashCode();
        float jitter01 = (h & 0xFFFF) / 65535f;   // [0,1]
        float jitter = jitter01 * 2f - 1f;        // [-1,1]

        // NOTE: '-' flips the arc direction (down instead of up)
        return (-curve * fanMaxDeg) + (jitter * jitterMaxDeg);
    }

    private float getHandYOffset(Card card) {
        int i = calcCardIndex(card);
        int n = cards.size();
        if (n <= 1) return 0f;

        float t = (i / (float) (n - 1)) * 2f - 1f; // [-1..+1]
        float arc = 0.15f; // tune (world units)

        // parabola: center highest (t=0), edges lowest (t=±1)
        return arc * (1f - t * t);
    }

    public void applyCard(Card card) {
        card.apply();
        applyingCard = card;

        DragableSlot slot = cards.get(card);
        slot.pulse();
        slot.wobble();
        Vector2 pos = slot.getRenderPos(new Vector2());

        ParticleManager.I().createBehindCardLayer(pos.x + CARD_WIDTH / 2, pos.y + CARD_HEIGHT / 2, ParticleType.RAINBOW);

        pos.x += 1.3f;
        pos.y += 1.8f;
        card.createPopupRunnable(pos).run();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                slot.startApplyAnimation(0.6f, () -> {
                    Hand.I().removeCardFromHand(card);
                });
            }
        }, 0.5f);
    }

    public void discardCard(Card card) {
        discardingCard = card;
        cards.get(card).startApplyAnimation(0.6f, () -> {
            discardingCard = null;
            Hand.I().discardCard(card);
        });
    }

}
