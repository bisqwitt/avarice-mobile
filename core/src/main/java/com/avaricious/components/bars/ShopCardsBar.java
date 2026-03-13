package com.avaricious.components.bars;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.upgrades.Deck;
import com.avaricious.upgrades.cards.Card;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopCardsBar {

    private final Rectangle buyBounds;
    private final Rectangle firstCardBounds = new Rectangle(1.65f, 12.85f, 142 / 85f, 190 / 85f);
    private final float CARD_OFFSET = 2f;

    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final Map<Card, DragableSlot> cards = new HashMap<>();
    private Card touchingCard = null;

    private final Map<Card, Boolean> cardVisibility = new HashMap<>();
    private boolean showCards = false;

    public ShopCardsBar(Rectangle buyBounds) {
        loadCards(Deck.I().randomUpgrades(3));
        this.buyBounds = buyBounds;
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching, float delta) {
        for (DragableSlot slot : cards.values()) slot.update(delta);

        if (touching && !wasTouching) {
            for (Map.Entry<Card, DragableSlot> entry : cards.entrySet()) {
                if (entry.getValue().getBounds().contains(mouse)) {
                    onCardTouchDown(entry.getKey(), mouse);
                    break;
                }
            }
        }

        if (touching && touchingCard != null) {
            onCardTouching(touchingCard, mouse);
        }

        if (!touching && wasTouching && touchingCard != null) {
            onCardTouchReleased(touchingCard);
        }
    }

    private void onCardTouchDown(Card card, Vector2 mouse) {
        touchingCard = card;
        cards.get(card).targetScale = 1.3f;
        cards.get(card).beginDrag(mouse.x, mouse.y, 0);

        PopupManager.I().createTooltip(card, cards.get(card).getRenderPos(new Vector2()));
    }

    private void onCardTouching(Card card, Vector2 mouse) {
        DragableSlot touchingSlot = cards.get(card);
        Vector2 cardRenderPos = touchingSlot.getRenderPos(new Vector2());

        touchingSlot.dragTo(mouse.x, mouse.y, 0);
        PopupManager.I().updateTooltip(
            new Vector2(cardRenderPos.x - 2f, cardRenderPos.y + 2.85f),
            true
        );
    }

    private void onCardTouchReleased(Card card) {
        DragableSlot dragableSlot = cards.get(card);
        if (buyBounds.contains(dragableSlot.getCardCenter())) {
            buyCard(card);
        } else {
            dragableSlot.endDrag(0);
            cards.get(card).targetScale = 1f;
        }
        touchingCard = null;
        PopupManager.I().killTooltip();
    }

    public void draw() {
        for (Card card : cards.keySet()) {
            if (touchingCard != card) drawCard(card);
        }
        if (touchingCard != null) drawCard(touchingCard);
    }

    private void drawCard(Card card) {
        if (!cardVisibility.get(card)) return;

        Rectangle bounds = cards.get(card).getBounds();
        DragableSlot slot = cards.get(card);

        final Vector2 position = slot.getRenderPos(new Vector2());
        final float alpha = slot.getAlpha();
        final float scale = slot.pulseScale()
            * slot.wobbleScale()
            * slot.getTargetScale();
        final float rotation = slot.wobbleAngleDeg() + slot.getDragTiltDeg();
        final ZIndex layer = card == touchingCard ? ZIndex.SHOP_CARD_TOUCHING : ZIndex.SHOP_CARD;

        final Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            jokerCardShadow,
            new Rectangle(
                position.x, position.y - (card == touchingCard ? 0.3f : 0.2f),
                bounds.width, bounds.height
            ),
            scale, rotation,
            layer, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha))
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            card.texture(),
            new Rectangle(position.x, position.y, bounds.width, bounds.height),
            scale, rotation,
            layer, new Color(1f, 1f, 1f, alpha)
        ));
    }

    public void loadCards(List<? extends Card> cards) {
        this.cards.clear();
        int index = 0;
        for (Card card : cards) {
            Rectangle bounds = new Rectangle(
                firstCardBounds.x + index * CARD_OFFSET, firstCardBounds.y,
                firstCardBounds.width, firstCardBounds.height
            );

            DragableSlot slot = new DragableSlot(bounds)
                .setTilt(200f, 20f);

            slot.pulse();
            slot.wobble();

            this.cards.put(card, slot);
            this.cardVisibility.put(card, showCards);
            index++;
        }
    }

    private void buyCard(Card card) {
        Deck.I().addUpgradeToDeck(card);
        cards.remove(card);
    }

    public void showCards() {
        showCards = true;
        int index = 0;
        for (Card card : cards.keySet()) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    cardVisibility.put(card, true);
                    cards.get(card).wobble();
                    cards.get(card).pulse();
                }
            }, index * 0.2f);
            index++;
        }
    }

    public boolean isDragging() {
        return touchingCard != null;
    }

}
