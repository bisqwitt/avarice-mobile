package com.avaricious.components;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableBody;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.effects.particle.ParticleManager;
import com.avaricious.effects.particle.ParticleType;
import com.avaricious.screens.ScreenManager;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.cards.AbstractCard;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.UiUtility;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HandUi {

    private final CardDestinationUI cardDestinationUI = new CardDestinationUI();

    private final float Y = 3.75f;
    private final float CARD_WIDTH = 142 / 85f;
    private final float CARD_HEIGHT = 190 / 85f;
    private final float CARD_OFFSET = 1.25f;

    private final TextureRegion jokerCard = Assets.I().get(AssetKey.JOKER_CARD);
    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final List<AbstractCard> cards = new ArrayList<>();

    private AbstractCard touchingCard = null;
    private AbstractCard applyingCard = null;
    private AbstractCard discardingCard = null;

    private List<? extends AbstractCard> pendingHand;

    public HandUi() {
        Hand.I().onChange(newHand -> pendingHand = newHand);
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        if (pendingHand != null) {
            loadCards(pendingHand);
            pendingHand = null;
        }

        if (pressed && !wasPressed) {
            List<AbstractCard> sorted = getEntriesSortedByX();
            Collections.reverse(sorted);
            for (AbstractCard card : sorted) {
                if (card.getBody().getBounds().contains(mouse)) {
                    onCardTouchDown(card, mouse);
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

    private void onCardTouchDown(AbstractCard card, Vector2 mouse) {
        touchingCard = card;
        card.getBody().targetScale = 1.3f;
        card.getBody().beginDrag(mouse.x, mouse.y, 0);

        PopupManager.I().createTooltip(card, card.getBody().getRenderPos(new Vector2()));
    }

    private void onCardTouching(AbstractCard card, Vector2 mouse) {
        DragableBody body = card.getBody();
        Vector2 cardRenderPos = body.getRenderPos(new Vector2());

        body.dragTo(mouse.x, mouse.y, 0);
        updateCardBounds();

        boolean hoveringSlotMachine = SlotMachine.windowBounds.contains(body.getCardCenter());
        boolean hoveringDeck = DeckUi.I().getHitBox().contains(body.getCardCenter());
        PopupManager.I().updateTooltip(
            new Vector2(cardRenderPos.x - (hoveringSlotMachine || hoveringDeck ? 0.9f : 2f), cardRenderPos.y + 2.85f),
            new Rectangle(0f, 0f, 9f, 8f).contains(mouse),
            hoveringSlotMachine, hoveringDeck);
    }

    private void onCardTouchReleased(AbstractCard card) {
        DragableBody body = card.getBody();
        if (SlotMachine.windowBounds.contains(body.getCardCenter())) {
            applyCard(card);
        } else if (DeckUi.I().getFirstCardBounds().contains(body.getCardCenter())) {
            discardCard(card);
        } else {
            body.endDrag(0);
            body.targetScale = 1f;
        }
        touchingCard = null;
        PopupManager.I().killTooltip();
        updateCardBounds();
    }

    public void draw(SpriteBatch batch, float delta) {
        cards.forEach(card -> card.getBody().update(delta));

        Vector2 touchingCardCenter = touchingCard != null ? touchingCard.getBody().getCardCenter() : new Vector2();
        cardDestinationUI.draw(batch, delta, touchingCardCenter, touchingCard != null);

        for (AbstractCard card : getEntriesSortedByX()) {
            if (touchingCard != card) drawCard(card);
        }

        if (touchingCard != null) drawCard(touchingCard);
        if (applyingCard != null) drawCard(applyingCard);
    }

    private void drawCard(AbstractCard card) {
        DragableBody body = card.getBody();
        Rectangle bounds = body.getBounds();

        float scale = body.getScale();
        final float rotation = body.getRotation()
            + (card != touchingCard && card != applyingCard ? getHandRotation(card) : 0);

        float alpha = body.getAlpha();
        if (DeckUi.I().getHitBox().contains(body.getCardCenter())) alpha -= 0.5f;

        Vector2 position = body.getRenderPos(new Vector2());

        Color shadowColor = Assets.I().shadowColor();
        Vector2 shadowOffset = UiUtility.calcShadowOffset(body.getCardCenter());
        Pencil.I().addDrawing(new TextureDrawing(
            jokerCardShadow,
            new Rectangle(
                position.x + shadowOffset.x, position.y - (card == touchingCard ? 0.3f : 0.2f),
                bounds.width, bounds.height
            ),
            scale, rotation,
            touchingCard == card || applyingCard == card ? ZIndex.HAND_UI_CARD_DRAGGING : ZIndex.HAND_UI_CARD, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(0.25f, alpha))
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            card.texture(),
            new Rectangle(position.x, position.y, bounds.width, bounds.height),
            scale, rotation,
            touchingCard == card || applyingCard == card ? ZIndex.HAND_UI_CARD_DRAGGING : ZIndex.HAND_UI_CARD, new Color(1f, 1f, 1f, alpha)
        ));
    }

    private void loadCards(List<? extends AbstractCard> newHand) {
        // Add new Cards
        for (AbstractCard card : newHand) {
            if (!cards.contains(card)) {

                // 1) Start at deck position
                Vector2 deckSpawn = DeckUi.I().getTopCardSpawnPos();

                Rectangle initialBounds = new Rectangle(
                    deckSpawn.x, deckSpawn.y,
                    CARD_WIDTH, CARD_HEIGHT
                );

                card.addBody(initialBounds);
                card.getBody().pulse();
                cards.add(card);
            }
        }

        // Remove old Cards
        List<AbstractCard> cardsToRemove = new ArrayList<>();
        for (AbstractCard card : cards) {
            if (!newHand.contains(card)) {
                cardsToRemove.add(card);
            }
        }
        for (AbstractCard card : cardsToRemove) {
            cards.remove(card);
        }

        updateCardBounds();
    }

    private void updateCardBounds() {
        for (AbstractCard card : cards) {
            if (card == touchingCard) continue;
            card.getBody().moveTo(new Vector2(calcCardX(card), Y + getHandYOffset(card)));
        }
    }

    private float calcCardX(AbstractCard card) {
        return calcFistX() + calcCardIndex(card) * (cards.size() != 7 ? CARD_OFFSET : CARD_OFFSET - 0.2f);
    }

    private float calcFistX() {
        List<AbstractCard> layoutCards = getCardsForLayout();
        int n = layoutCards.size();
        if (n == 0) return 0;

        float screenWidth = ScreenManager.getViewport().getWorldWidth();
        float handWidth = (n - 1) * (cards.size() != 7 ? CARD_OFFSET : CARD_OFFSET - 0.2f) + CARD_WIDTH;
        return (screenWidth - handWidth) / 2f;
    }

    private int calcCardIndex(AbstractCard card) {
        List<AbstractCard> sorted = getEntriesSortedByX();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i) == card) return i;
        }
        return -1;
    }

    private List<AbstractCard> getEntriesSortedByX() {
        List<AbstractCard> sorted = new ArrayList<>(getCardsForLayout());
        sorted.sort(Comparator.comparingDouble(card -> card.getBody().getRenderPos(new Vector2()).x));
        return sorted;
    }

    private float getHandRotation(AbstractCard card) {
        List<AbstractCard> layoutCards = getEntriesSortedByX();
        int n = layoutCards.size();
        if (n <= 1) return 0f;

        int i = layoutCards.indexOf(card);
        if (i < 0) return 0f;

        float t = (i / (float) (n - 1)) * 2f - 1f; // [-1..+1]

        float fanMaxDeg = 6f;
        float jitterMaxDeg = 1.0f;

        float curve = t * t * t;

        int h = card.hashCode();
        float jitter01 = (h & 0xFFFF) / 65535f;
        float jitter = jitter01 * 2f - 1f;

        return (-curve * fanMaxDeg) + (jitter * jitterMaxDeg);
    }

    private float getHandYOffset(AbstractCard card) {
        List<AbstractCard> layoutCards = getEntriesSortedByX();
        int n = layoutCards.size();
        if (n <= 1) return 0f;

        int i = layoutCards.indexOf(card);
        if (i < 0) return 0f;

        float t = (i / (float) (n - 1)) * 2f - 1f;
        float arc = 0.15f;

        return arc * (1f - t * t);
    }

    private List<AbstractCard> getCardsForLayout() {
        boolean touchingOverSlotMachine =
            touchingCard != null &&
                (SlotMachine.windowBounds.contains(touchingCard.getBody().getCardCenter())
                    || DeckUi.I().getFirstCardBounds().contains(touchingCard.getBody().getCardCenter()));

        return cards.stream()
            .filter(card -> !(touchingOverSlotMachine && card == touchingCard))
            .filter(card -> card != applyingCard)
            .collect(Collectors.toList());
    }

    public void applyCard(AbstractCard card) {
        card.apply();
        applyingCard = card;

        DragableBody body = card.getBody();
        body.pulse();
        Vector2 pos = body.getRenderPos(new Vector2());

        ParticleManager.I().create(pos.x + CARD_WIDTH / 2, pos.y + CARD_HEIGHT / 2, ParticleType.RAINBOW, 0.03f, ZIndex.CARD_APPLY_PARTICLES);

        pos.x += 1.3f;
        pos.y += 1.8f;
        card.createPopupRunnable(pos).run();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                body.startApplyAnimation(0.6f, () -> {
                    Hand.I().removeCardFromHand(card);
                    applyingCard = null;
                });
            }
        }, 0.5f);
    }

    public void discardCard(AbstractCard card) {
        discardingCard = card;
        card.getBody().startApplyAnimation(0.6f, () -> {
            discardingCard = null;
            Hand.I().discardCard(card);
        });
    }

    public void discardAllCards() {
        cards.forEach(this::discardCard);
    }

}
