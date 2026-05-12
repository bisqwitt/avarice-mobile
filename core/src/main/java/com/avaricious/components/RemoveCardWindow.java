package com.avaricious.components;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.popups.TooltipPopup;
import com.avaricious.components.slot.DragableBody;
import com.avaricious.components.texts.PermanentlyRemoveACardText;
import com.avaricious.items.upgrades.Deck;
import com.avaricious.items.upgrades.Hand;
import com.avaricious.items.upgrades.cards.AbstractCard;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class RemoveCardWindow {

    private final PermanentlyRemoveACardText title = new PermanentlyRemoveACardText();
    private final List<AbstractCard> cards = new ArrayList<>();

    private boolean open = false;

    private AbstractCard selectedCard = null;
    private AbstractCard touchingCard = null;

    private final Vector2 mouseTouchdownLocation = new Vector2();
    private TooltipPopup tooltipPopup;

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching) {
        if (!open) return;

        if (touching && !wasTouching) {
            mouseTouchdownLocation.set(mouse.x, mouse.y);
            for (AbstractCard card : cards) {
                if (card.getBody().getBounds().contains(mouse)) {
                    onCardTouchdown(card, mouse);
                    return;
                }
            }
            deselectCard(true);
        }

        if (touching && touchingCard != null) {
            onCardTouching(touchingCard, mouse);
        }

        if (!touching && wasTouching && touchingCard != null) {
            onCardTouchReleased(touchingCard, mouse);
        }

        if (touchingCard != null || selectedCard != null) {
            AbstractCard card = touchingCard == null ? selectedCard : touchingCard;
            Vector2 renderPos = card.getBody().getRenderPos(new Vector2());
            PopupManager.I().updateTooltip(
                new Vector2(renderPos.x - 2f, renderPos.y + 2.85f), true
            );
        }
    }

    private void onCardTouchdown(AbstractCard card, Vector2 mouse) {
        if (selectedCard != null && card != selectedCard) deselectCard(false);
        touchingCard = card;
        card.getBody().targetScale = 1.3f;
        card.getBody().beginDrag(mouse.x, mouse.y, 0);

        if (selectedCard == null)
            tooltipPopup = PopupManager.I().createTooltip(card, card.getBody().getRenderPos(new Vector2()));
    }

    private void onCardTouching(AbstractCard card, Vector2 mouse) {
        card.getBody().dragTo(mouse.x, mouse.y, 0);
    }

    private void onCardTouchReleased(AbstractCard card, Vector2 mouse) {
        DragableBody body = card.getBody();
        body.endDrag(0);
        boolean isClick = mouseTouchdownLocation.dst(mouse) <= 0.2f * 0.2f;
        if (isClick) {
            if (selectedCard == card) deselectCard(true);
            else {
                if (selectedCard != null) deselectCard(false);
                selectedCard = card;
            }
        } else selectedCard = card;
        touchingCard = null;
    }

    private void update(float delta) {

    }

    public void draw(float delta) {
        if (!open) return;

//        Pencil.I().addDrawing(new FontDrawing(Assets.I().getTitleFont(), ));
        title.draw(delta);
        cards.forEach(this::drawCard);
    }

    private void drawCard(AbstractCard card) {
        DragableBody body = card.getBody();
        Vector2 pos = body.getRenderPos(new Vector2());
        float scale = body.getScale();
        float rotation = body.getRotation();
        float width = AbstractCard.WIDTH / 90f;
        float height = AbstractCard.HEIGHT / 90f;
        ZIndex zIndex = card == touchingCard || card == selectedCard ? ZIndex.PACK_OPENING_SELECTED : ZIndex.PACK_OPENING;

        Pencil.I().addDrawing(new TextureDrawing(
            card.shadowTexture(),
            pos.x, pos.y - 0.2f, width, height,
            scale, rotation, zIndex, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            card.texture(),
            pos.x, pos.y, width, height,
            scale, rotation, zIndex
        ));
    }

    private void deselectCard(boolean killTooltip) {
        if (selectedCard == null) return;
        selectedCard.getBody().targetScale = 1f;
        selectedCard.getBody().setIdleEffectsEnabled(true);
        selectedCard = null;
        if (killTooltip) {
            PopupManager.I().killTooltip(tooltipPopup);
            tooltipPopup = null;
        }
    }

    public void open() {
        open = true;

        cards.clear();
        cards.addAll(Hand.I().getHand());
        cards.addAll(Deck.I().getDeck());

        List<Vector2> positions = new ArrayList<>();
        for (int col = 5; col > 0; col--) {
            for (int row = 0; row < 4; row++) {
                positions.add(new Vector2(0.7f + row * 2f, 1f + col * 2.5f));
            }
        }

        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).getBody().getPos().set(positions.get(i));
        }

        Pencil.I().toggleDarkenEverythingBehindLayer(ZIndex.PACK_OPENING);
    }

    public boolean isOpen() {
        return open;
    }
}
