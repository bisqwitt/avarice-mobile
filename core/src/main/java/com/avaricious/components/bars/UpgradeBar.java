package com.avaricious.components.bars;

import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.Body;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.cards.AbstractCard;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UpgradeBar {

    private final Rectangle cardRectangle;
    private final float offset;

    private ZIndex zIndex = ZIndex.UPGRADE_BAR;

    protected final Map<Upgrade, Rectangle> cardBounds = new HashMap<>();
    protected final Map<Upgrade, Body> cardAnimationManagers = new HashMap<>();

    private Upgrade hoveringKey = null;

    private final boolean tooltipOnTopOfCard;

    protected Runnable onCardClicked;

    public UpgradeBar(List<? extends Upgrade> Cards, Rectangle cardRectangle, float offset, boolean tooltipOnTop) {
        tooltipOnTopOfCard = tooltipOnTop;

        this.cardRectangle = cardRectangle;
        this.offset = offset;

        for (int i = 0; i < Cards.size(); i++) {
            Upgrade upgrade = Cards.get(i);
            cardBounds.put(upgrade, new Rectangle(cardRectangle.x + (i * offset), cardRectangle.y, cardRectangle.width, cardRectangle.height));
            cardAnimationManagers.put(upgrade, new Body(new Vector2(cardRectangle.x, cardRectangle.y)));
        }
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        hoveringKey = null;
        Upgrade[] clickedCard = new Upgrade[1];
        for (Map.Entry<Upgrade, Rectangle> entry : cardBounds.entrySet()) {
            Upgrade upgrade = entry.getKey();
            Rectangle rectangle = entry.getValue();

            boolean hovered = rectangle.contains(mouse);
            boolean selected = wasPressed && !pressed && rectangle.contains(mouse);

            Body cardBody = cardAnimationManagers.get(upgrade);
            cardBody.targetScale = pressed && rectangle.contains(mouse) ? 1.2f
                : hovered ? 1.075f : 1f;

            cardBody.update(delta);

            if (hovered) hoveringKey = upgrade;
            if (selected) clickedCard[0] = hoveringKey;
        }

        if (hoveringKey != null)
            PopupManager.I().createTooltip(hoveringKey,
                new Vector2(getHoveringRectangle().x - 1f, getHoveringRectangle().y + (tooltipOnTopOfCard ? 2 : -2)));

        if (clickedCard[0] != null) {
            onUpgradeClicked(clickedCard[0]);
        }
    }

    /**
     * Draw the card using Slot's wobble/scale.
     */
    public void draw(SpriteBatch batch) {
        // combined scale (identical idea to SlotMachine)
        for (Map.Entry<Upgrade, Rectangle> entry : cardBounds.entrySet()) {
            Upgrade upgrade = entry.getKey();
            Rectangle rectangle = entry.getValue();

            Body cardBody = cardAnimationManagers.get(upgrade);
            float s = cardBody.getScale();

            float drawW = rectangle.width * s;
            float drawH = rectangle.height * s;

            // center scaling around the original x/y
            float adjX = rectangle.x - (drawW - rectangle.width) / 2f;
            float adjY = rectangle.y - (drawH - rectangle.height) / 2f;

            float rotation = cardBody.getRotation();

            drawCard(batch, upgrade, new Rectangle(adjX, adjY, drawW, drawH), s, rotation);
        }
    }

    protected void drawCard(SpriteBatch batch, Upgrade upgrade, Rectangle bounds, float scale, float rotation) {
        // draw shadow (also scaled and rotated)
        Pencil.I().addDrawing(new TextureDrawing(
            getShadow(upgrade),
            new Rectangle(bounds.x + 0.1f, bounds.y - 0.1f, bounds.width, bounds.height),
            1f, rotation, ZIndex.UPGRADE_BAR, Assets.I().shadowColor()
        ));
        Pencil.I().addDrawing(new TextureDrawing(
            getTexture(upgrade),
            bounds, 1f, rotation,
            zIndex
        ));
    }

    public void loadUpgrades(List<? extends Upgrade> upgrades) {
        cardBounds.clear();
        cardAnimationManagers.clear();

        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade Card = upgrades.get(i);
            cardBounds.put(Card, new Rectangle(cardRectangle.x + (i * offset), cardRectangle.y, cardRectangle.width, cardRectangle.height));
            cardAnimationManagers.put(Card, new Body(new Vector2(cardRectangle.x, cardRectangle.y)));
        }
    }

    protected abstract void onUpgradeClicked(Upgrade clickedCard);

    protected abstract TextureRegion getTexture(Upgrade Card);

    protected abstract TextureRegion getShadow(Upgrade Card);

    public Upgrade getHoveringCard() {
        return hoveringKey;
    }

    public Rectangle getHoveringRectangle() {
        return cardBounds.get(hoveringKey);
    }

    public Body getSlotByCard(AbstractCard Card) {
        return cardAnimationManagers.get(Card);
    }

    public Rectangle getRectangleByCard(AbstractCard Card) {
        return cardBounds.get(Card);
    }

    public void setOnUpgradeClickedAndAnimationEnded(Runnable onCardClicked) {
        this.onCardClicked = onCardClicked;
    }

    protected Rectangle getCardRectangle() {
        return cardRectangle;
    }

    public void setzIndex(ZIndex zIndex) {
        this.zIndex = zIndex;
    }
}
