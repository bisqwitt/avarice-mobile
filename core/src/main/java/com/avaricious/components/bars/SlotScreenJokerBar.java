package com.avaricious.components.bars;

import com.avaricious.cards.Card;
import com.avaricious.components.CardDestinationUI;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.upgrades.Hand;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SlotScreenJokerBar {

    private static SlotScreenJokerBar instance;

    public static SlotScreenJokerBar I() {
        return instance == null ? instance = new SlotScreenJokerBar() : instance;
    }

    private final CardDestinationUI cardDestinationUI = new CardDestinationUI();

    private final TextureRegion jokerTexture = Assets.I().get(AssetKey.JOKER_CARD);
    private final TextureRegion jokerShadowTexture = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);
    private final TextureRegion blueGreenTexture = Assets.I().get(AssetKey.BLUE_GREEN_PIXEL);

    private final Map<Card, Rectangle> jokerBounds = new LinkedHashMap<>();
    private final Map<Card, DragableSlot> jokerAnimationManagers = new LinkedHashMap<>();

    private final List<Rectangle> jokerRectangles;

    private Card selectedUpgrade;

    private Card activeDragUpgrade = null;
    private final Vector2 pressStart = new Vector2();
    private boolean movedSincePress = false;

    // world units; tune to taste
    private final float dragThreshold = 0.12f;

    private boolean reloadRequested = false;
    private List<? extends Card> pendingHand = null;

    public SlotScreenJokerBar() {
        jokerRectangles = Arrays.asList(
            new Rectangle(0.6f, 3.6f, 142 / 95f, 190 / 95f),
            new Rectangle(2.6f, 3.6f, 142 / 95f, 190 / 95f),
            new Rectangle(4.6f, 3.6f, 142 / 95f, 190 / 95f),
            new Rectangle(6.6f, 3.6f, 142 / 95f, 190 / 95f),
            new Rectangle(0.6f, 1.3f, 142 / 95f, 190 / 95f),
            new Rectangle(2.6f, 1.3f, 142 / 95f, 190 / 95f),
            new Rectangle(4.6f, 1.3f, 142 / 95f, 190 / 95f),
            new Rectangle(6.6f, 1.3f, 142 / 95f, 190 / 95f)
        );

        loadJokers(Hand.I().getHand());
        Hand.I().onChange(newHand -> {
            reloadRequested = true;
            pendingHand = newHand;
        });
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        if (reloadRequested) {
            reloadRequested = false;
            loadJokers(pendingHand);
            pendingHand = null;

            if (selectedUpgrade != null && !jokerAnimationManagers.containsKey(selectedUpgrade))
                selectedUpgrade = null;

            if (activeDragUpgrade != null && !jokerAnimationManagers.containsKey(activeDragUpgrade))
                activeDragUpgrade = null;
        }

        // --- Hover updates (correct: only hovered if mouse inside) ---
        for (Map.Entry<Card, Rectangle> entry : jokerBounds.entrySet()) {
            Card upgrade = entry.getKey();
            Rectangle bounds = entry.getValue();
            DragableSlot slot = jokerAnimationManagers.get(upgrade);

            boolean hovered = bounds.contains(mouse);
            slot.updateHoverWobble(hovered, delta);

            // If you want pulse to react to selection state:
            slot.updatePulse(selectedUpgrade == upgrade, delta);
        }

        // --- Press: begin drag on top-most hit card (selected drawn last) ---
        if (pressed && !wasPressed) {
            pressStart.set(mouse);
            movedSincePress = false;

            // Prefer selected upgrade if pressed on it (since it is visually on top)
            if (selectedUpgrade != null) {
                Rectangle b = jokerBounds.get(selectedUpgrade);
                if (b != null && b.contains(mouse)) {
                    activeDragUpgrade = selectedUpgrade;
                    jokerAnimationManagers.get(activeDragUpgrade).beginDrag(mouse.x, mouse.y, 0);
                    return;
                }
            }

            // Otherwise find any hit
            for (Map.Entry<Card, Rectangle> entry : jokerBounds.entrySet()) {
                Card upgrade = entry.getKey();
                Rectangle bounds = entry.getValue();

                if (bounds.contains(mouse)) {
                    activeDragUpgrade = upgrade;
                    jokerAnimationManagers.get(upgrade).beginDrag(mouse.x, mouse.y, 0);
                    return;
                }
            }
        }

        // --- Dragging: update drag offset while pressed ---
        if (pressed && activeDragUpgrade != null) {
            if (!movedSincePress && pressStart.dst2(mouse) > dragThreshold * dragThreshold) {
                movedSincePress = true;
            }
            jokerAnimationManagers.get(activeDragUpgrade).dragTo(mouse.x, mouse.y, 0);
        }

        // --- Release: end drag, and if it was basically a click, toggle selection ---
        if (!pressed && wasPressed) {
            if (activeDragUpgrade != null) {
                DragableSlot dragableSlot = jokerAnimationManagers.get(activeDragUpgrade);
                if (SlotMachine.windowBounds.contains(dragableSlot.getCardCenter())) {
                    applyCard(activeDragUpgrade);
                } else if (cardDestinationUI.isOverDumpster(dragableSlot.getCardCenter())) {
                    discardCard(activeDragUpgrade);
                }
                dragableSlot.endDrag(0);

                if (!movedSincePress) {
                    Card upgrade = activeDragUpgrade;
                    selectedUpgrade = (selectedUpgrade != upgrade) ? upgrade : null;
                }
            }
            activeDragUpgrade = null;
        }

        // --- Per-frame updates for draggable physics/tilt/return ---
        for (DragableSlot slot : jokerAnimationManagers.values()) {
            slot.update(delta);
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        boolean dragging = activeDragUpgrade != null;
        Vector2 center = dragging ? jokerAnimationManagers.get(activeDragUpgrade).getCardCenter() : new Vector2();

        cardDestinationUI.draw(batch, delta, center, dragging);

        batch.setColor(Assets.I().shadowColor());
        for (Rectangle rectangle : jokerRectangles) {
            batch.draw(blueGreenTexture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        batch.setColor(1f, 1f, 1f, 1f);

        for (Map.Entry<Card, Rectangle> entry : jokerBounds.entrySet()) {
            if (entry.getKey() != selectedUpgrade && entry.getKey() != activeDragUpgrade) {
                drawJokerCard(batch, entry.getKey(), entry.getValue());
            }
        }

        if (activeDragUpgrade != null) {
            drawJokerCard(batch, activeDragUpgrade, jokerBounds.get(activeDragUpgrade));
        }

        if (selectedUpgrade != null)
            drawJokerCard(batch, selectedUpgrade, jokerBounds.get(selectedUpgrade));
    }

    private void drawJokerCard(SpriteBatch batch, Card upgrade, Rectangle bounds) {
        DragableSlot slot = jokerAnimationManagers.get(upgrade);

        float selectedScale = (selectedUpgrade == upgrade) ? 1.3f : 1f;

        float s = slot.pulseScale()
            * slot.wobbleScale()
            * slot.getDragScaleMul()
            * slot.getExtraScaleMul()
            * selectedScale;

        float r = slot.wobbleAngleDeg() + slot.getDragTiltDeg();

        float originX = bounds.width * 0.5f;
        float originY = bounds.height * 0.5f;

        // IMPORTANT: render position can differ from bounds.x/y while dragging
        Vector2 p = slot.getRenderPos(new Vector2());
        float alpha = slot.getAlpha();

        if (selectedUpgrade == upgrade || slot.isDragging()) {
            Color shadowColor = Assets.I().shadowColor();
            batch.setColor(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(0.25f, alpha));
            batch.draw(jokerShadowTexture,
                p.x, p.y - 0.2f,
                originX, originY,
                bounds.width, bounds.height,
                s, s,
                r);
            batch.setColor(1f, 1f, 1f, 1f);

            if (!slot.isDragging())
                PopupManager.I().renderTooltip(selectedUpgrade, p.x - 2f, p.y + 2.65f);
        }

        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(
            jokerTexture,
            p.x, p.y,
            originX, originY,
            bounds.width, bounds.height,
            s, s,
            r);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void loadJokers(List<? extends Card> upgrades) {
        jokerBounds.clear();
        jokerAnimationManagers.clear();

        for (int i = 0; i < upgrades.size(); i++) {
            Card upgrade = upgrades.get(i);
            Rectangle rectangle = jokerRectangles.get(i);

            jokerBounds.put(upgrade, rectangle);

            jokerAnimationManagers.put(
                upgrade,
                new DragableSlot(
                    new Vector2(rectangle.x, rectangle.y),
                    rectangle.width,
                    rectangle.height
                ).setTilt(200f, 10f)
                    .setDragScale(1.3f)
                // optional tuning:
                //.setFollow(30f, 18f)
                //.setTilt(14f, 12f)
                //.setDragScale(1.08f)
            );
        }
    }

    public void applyCard(Card card) {
        card.apply();

        DragableSlot slot = jokerAnimationManagers.get(card);
        slot.pulse();
        slot.wobble();
        Vector2 pos = slot.getRenderPos(new Vector2());
        pos.x += 1.3f;
        pos.y += 1.8f;
        card.createPopupRunnable(pos).run();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Start animation and remove only when finished
                slot.startApplyTo(0.6f, () -> Hand.I().removeCardFromHand(card));
            }
        }, 0.5f);
    }

    public void discardCard(Card card) {
        jokerAnimationManagers.get(card).startApplyTo(0.6f, () -> Hand.I().removeCardFromHand(card));
    }

    public Rectangle getBoundsByUpgrade(Upgrade upgrade) {
        return jokerBounds.get(upgrade);
    }

    public Slot getSlotByUpgrade(Upgrade upgrade) {
        return jokerAnimationManagers.get(upgrade);
    }


}
