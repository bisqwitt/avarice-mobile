package com.avaricious.components.bars;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableBody;
import com.avaricious.upgrades.Deck;
import com.avaricious.upgrades.cards.AbstractCard;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopCardsBar {

    private final Rectangle buyBounds;
    private final Rectangle firstCardBounds = new Rectangle(1.55f, 12.725f, 142 / 80f, 190 / 80f);
    private final float CARD_OFFSET = 2f;

    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final Map<AbstractCard, DragableBody> cards = new HashMap<>();
    private AbstractCard touchingCard = null;

    public ShopCardsBar(Rectangle buyBounds) {
        loadCards(Deck.I().randomUpgrades(3));
        this.buyBounds = buyBounds;
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching, float delta) {
        for (DragableBody slot : cards.values()) slot.update(delta);

        if (touching && !wasTouching) {
            for (Map.Entry<AbstractCard, DragableBody> entry : cards.entrySet()) {
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

    private void onCardTouchDown(AbstractCard card, Vector2 mouse) {
        touchingCard = card;
        cards.get(card).targetScale = 1.3f;
        cards.get(card).beginDrag(mouse.x, mouse.y, 0);

        PopupManager.I().createTooltip(card, cards.get(card).getRenderPos(new Vector2()));
    }

    private void onCardTouching(AbstractCard card, Vector2 mouse) {
        DragableBody touchingSlot = cards.get(card);
        Vector2 cardRenderPos = touchingSlot.getRenderPos(new Vector2());

        touchingSlot.dragTo(mouse.x, mouse.y, 0);
        PopupManager.I().updateTooltip(
            new Vector2(cardRenderPos.x - 2f, cardRenderPos.y + 2.85f),
            true
        );
    }

    private void onCardTouchReleased(AbstractCard card) {
        DragableBody dragableBody = cards.get(card);
        if (buyBounds.contains(dragableBody.getCardCenter())) {
            buyCard(card);
        } else {
            dragableBody.endDrag(0);
            cards.get(card).targetScale = 1f;
        }
        touchingCard = null;
        PopupManager.I().killTooltip();
    }

    public void draw() {
        for (AbstractCard card : cards.keySet()) {
            if (touchingCard != card) drawCard(card);
        }
        if (touchingCard != null) drawCard(touchingCard);
    }

    private void drawCard(AbstractCard card) {
        Rectangle bounds = cards.get(card).getBounds();
        DragableBody slot = cards.get(card);

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

    public void loadCards(List<? extends AbstractCard> cards) {
        this.cards.clear();
        int index = 0;
        for (AbstractCard card : cards) {
            Rectangle bounds = new Rectangle(
                firstCardBounds.x + index * CARD_OFFSET, firstCardBounds.y,
                firstCardBounds.width, firstCardBounds.height
            );

            DragableBody slot = new DragableBody(bounds)
                .setTilt(200f, 20f);

            slot.pulse();
            slot.wobble();

            this.cards.put(card, slot);
            index++;
        }
    }

    private void buyCard(AbstractCard card) {
        Deck.I().addCardToDeck(card);
        cards.remove(card);
    }

    public void setY(float y) {
        cards.values().forEach(body -> body.getPos().y = y);
    }

    public boolean isDragging() {
        return touchingCard != null;
    }

}
