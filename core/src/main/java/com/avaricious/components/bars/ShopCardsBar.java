package com.avaricious.components.bars;

import com.avaricious.CreditManager;
import com.avaricious.CreditNumber;
import com.avaricious.components.popups.BoughtPopup;
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
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopCardsBar {

    private final Rectangle buyBounds;
    private final Rectangle firstCardBounds = new Rectangle(1.55f, 12.725f, 142 / 80f, 190 / 80f);
    private final float CARD_OFFSET = 2f;

    private final TextureRegion jokerCardShadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);
    private final TextureRegion priceBox = Assets.I().get(AssetKey.PRICE_BOX);

    private final Map<AbstractCard, CreditNumber> cardPriceMap = new HashMap<>();
    private AbstractCard touchingCard = null;
    private AbstractCard boughtCard = null;

    public ShopCardsBar(Rectangle buyBounds) {
        loadCards(Deck.I().randomUpgrades(3));
        this.buyBounds = buyBounds;
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching, float delta) {
        for (AbstractCard card : cardPriceMap.keySet()) card.getBody().update(delta);
        if (boughtCard != null) boughtCard.getBody().update(delta);

        if (touching && !wasTouching) {
            for (AbstractCard card : cardPriceMap.keySet()) {
                if (card.getBody().getBounds().contains(mouse)) {
                    onCardTouchDown(card, mouse);
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
        card.getBody().targetScale = 1.3f;
        card.getBody().beginDrag(mouse.x, mouse.y, 0);

        PopupManager.I().createTooltip(card, card.getBody().getRenderPos(new Vector2()));
    }

    private void onCardTouching(AbstractCard card, Vector2 mouse) {
        DragableBody body = card.getBody();
        Vector2 cardRenderPos = body.getRenderPos(new Vector2());

        body.dragTo(mouse.x, mouse.y, 0);
        PopupManager.I().updateTooltip(
            new Vector2(cardRenderPos.x - 2f, cardRenderPos.y + 2.85f),
            true
        );
    }

    private void onCardTouchReleased(AbstractCard card) {
        DragableBody body = card.getBody();
        if (buyBounds.contains(body.getCardCenter())) {
            buyCard(card);
        } else {
            body.endDrag(0);
            body.targetScale = 1f;
        }
        touchingCard = null;
        PopupManager.I().killTooltip();
    }

    public void draw(float delta) {
        for (AbstractCard card : cardPriceMap.keySet()) {
            if (touchingCard != card) drawCard(card, delta);
        }
        if (touchingCard != null) drawCard(touchingCard, delta);
        if (boughtCard != null) drawCard(boughtCard, delta);
    }

    private void drawCard(AbstractCard card, float delta) {
        Rectangle bounds = card.getBody().getBounds();
        DragableBody body = card.getBody();

        final Vector2 position = body.getRenderPos(new Vector2());
        final float alpha = body.getAlpha();
        final float scale = body.getScale();
        final float rotation = body.getRotation();
        final ZIndex layer = card == touchingCard ? ZIndex.SHOP_CARD_TOUCHING : ZIndex.SHOP_CARD;

        final Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            jokerCardShadow,
            new Rectangle(
                position.x, position.y - (card == touchingCard ? 0.3f : 0.2f),
                bounds.width, bounds.height
            ), scale, rotation,
            layer, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha))
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            card.texture(),
            new Rectangle(position.x, position.y, bounds.width, bounds.height),
            scale, rotation,
            layer, new Color(1f, 1f, 1f, alpha)
        ));
        if (card != touchingCard && card != boughtCard) {
            cardPriceMap.get(card).setZIndex(layer);
            cardPriceMap.get(card).getBounds().y = position.y + 2.6f;
            cardPriceMap.get(card).draw(delta, 1f, rotation);
        }
    }

    public void loadCards(List<? extends AbstractCard> cards) {
        this.cardPriceMap.clear();
        int index = 0;
        for (AbstractCard card : cards) {
            Rectangle bounds = new Rectangle(
                firstCardBounds.x + index * CARD_OFFSET, firstCardBounds.y,
                firstCardBounds.width, firstCardBounds.height
            );

            card.addBody(bounds);

            this.cardPriceMap.put(card, new CreditNumber(card.price(), new Rectangle(bounds.x + 0.5f, bounds.y, 7 / 20f, 11 / 20f), 0.4f));
            index++;
        }
    }

    private void buyCard(AbstractCard card) {
        Deck.I().addCardToDeck(card);
        CreditManager.I().pay(card.price());
        boughtCard = card;
        cardPriceMap.remove(card);

        card.getBody().pulse();

        Vector2 renderPos = card.getBody().getRenderPos(new Vector2());
        PopupManager.I().spawnTextPopup(new BoughtPopup(new Vector2((renderPos.x + card.getBody().getBounds().width / 2f) - BoughtPopup.WIDTH / 2f, renderPos.y + 3f), ZIndex.SHOP_CARD_TOUCHING));
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                boughtCard = null;
            }
        }, 1);
    }

    public void setY(float y) {
        cardPriceMap.keySet().forEach(body -> body.getBody().getPos().y = y);
    }

    public boolean isDragging() {
        return touchingCard != null;
    }

}
