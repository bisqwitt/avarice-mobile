package com.avaricious.components;

import com.avaricious.DevTools;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.upgrades.rings.AbstractRing;
import com.avaricious.upgrades.rings.triggerable.multAdditions.pattern.ThreeOfAKindMultiAdditionRing;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.PointsForEveryRingHit;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CherryValueStackRing;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.UiUtility;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RingBar {

    private static RingBar instance;

    public static RingBar I() {
        return instance == null ? instance = new RingBar() : instance;
    }

    private final Rectangle firstRingBounds = new Rectangle(0.35f, 7.25f, 1.25f, 1.25f);
    private final float RING_OFFSET = 1.75f;

    private final TextureRegion shadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final Map<AbstractRing, DragableSlot> rings = new HashMap<>();
    private final Map<AbstractRing, Integer> ringIndex = new HashMap<>();

    private AbstractRing touchingRing = null;

    private RingBar() {
        if (DevTools.testRings) {
            addRing(new ThreeOfAKindMultiAdditionRing());
            addRing(new PointsForEveryRingHit());
            addRing(new CherryValueStackRing());
        }
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        for (DragableSlot slot : rings.values()) slot.update(delta);

        if (pressed && !wasPressed) {
            for (Map.Entry<AbstractRing, DragableSlot> entry : rings.entrySet()) {
                if (entry.getValue().getBounds().contains(mouse)) {
                    onRingTouchDown(entry.getKey(), mouse);
                    break;
                }
            }
        }

        if (pressed && touchingRing != null) {
            onRingTouching(touchingRing, mouse);
        }

        if (!pressed && wasPressed && touchingRing != null) {
            onRingTouchReleased(touchingRing);
        }
    }

    private void onRingTouchDown(AbstractRing ring, Vector2 mouse) {
        touchingRing = ring;
        rings.get(ring).targetScale = 1.3f;
        rings.get(ring).beginDrag(mouse.x, mouse.y, 0);

        PopupManager.I().createTooltip(ring, rings.get(ring).getRenderPos(new Vector2()));
    }

    private void onRingTouching(AbstractRing ring, Vector2 mouse) {
        DragableSlot touchingSlot = rings.get(ring);
        Vector2 ringRenderPos = touchingSlot.getRenderPos(new Vector2());

        touchingSlot.dragTo(mouse.x, mouse.y, 0);
        PopupManager.I().updateTooltip(
            new Vector2(ringRenderPos.x - 2f, ringRenderPos.y + 2.85f),
            true
        );

        updateRingIndexes();
    }

    private void onRingTouchReleased(AbstractRing ring) {
        DragableSlot dragableSlot = rings.get(ring);
        dragableSlot.endDrag(0);
        rings.get(ring).targetScale = 1f;
        touchingRing = null;
        PopupManager.I().killTooltip();
    }

    public void draw() {
//        for (int i = 0; i < 5; i++) {
//            Pencil.I().addDrawing(new TextureDrawing(shadow,
//                new Rectangle(0.25f + i * 1.75f, 7.05f, 1.5f, 1.5f),
//                ZIndex.RING_BAR, Assets.I().shadowColor()
//            ));
//        }

        for (AbstractRing ring : rings.keySet()) {
            drawRing(ring);
        }
    }

    private void drawRing(AbstractRing ring) {
        DragableSlot slot = rings.get(ring);
        Rectangle bounds = slot.getBounds();

        float scale = slot.pulseScale()
            * slot.wobbleScale()
            * slot.getTargetScale();
        float rotation = slot.wobbleAngleDeg() + slot.getDragTiltDeg();

        float alpha = slot.getAlpha();
        Vector2 position = slot.getRenderPos(new Vector2());

        Color shadowColor = Assets.I().shadowColor();
        Vector2 shadowOffset = UiUtility.calcShadowOffset(slot.getCardCenter());
        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().get(ring.keySet().getShadowKey()),
            new Rectangle(position.x + shadowOffset.x, position.y - (ring == touchingRing ? 0.3f : 0.2f),
                bounds.width, bounds.height
            ), scale, rotation, touchingRing == ring ? ZIndex.RING_BAR_DRAGGING : ZIndex.RING_BAR, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(0.25f, alpha))));
        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().get(ring.keySet().getTextureKey()),
            new Rectangle(position.x, position.y, bounds.width, bounds.height),
            scale, rotation, touchingRing == ring ? ZIndex.RING_BAR_DRAGGING : ZIndex.RING_BAR, new Color(1f, 1f, 1f, alpha)
        ));
    }

    public void addRing(AbstractRing ring) {
        int index = rings.size();
        Rectangle bounds = new Rectangle(firstRingBounds).setX(firstRingBounds.x + index * RING_OFFSET);
        rings.put(ring, new DragableSlot(bounds).setTilt(200f, 20f));
        ringIndex.put(ring, index);
    }

    public <T extends AbstractRing> T getRingByClass(Class<T> ringClass) {
        for (AbstractRing ring : rings.keySet()) {
            if (ringClass.isInstance(ring)) return (T) ring;
        }
        return null;
    }


    public <T> List<T> getRingsOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (AbstractRing ring : rings.keySet()) {
            if (type.isInstance(ring)) result.add(type.cast(ring));
        }
        return result;
    }

    public DragableSlot getSlotByRing(AbstractRing ring) {
        return rings.get(ring);
    }

    public boolean ringOwned(Class<? extends AbstractRing> ringClass) {
        for (AbstractRing ring : rings.keySet()) {
            if (ringClass.isInstance(ring)) return true;
        }
        return false;
    }

    private void updateRingIndexes() {
        for (Map.Entry<AbstractRing, DragableSlot> entry : rings.entrySet()) {
            AbstractRing ring = entry.getKey();
            DragableSlot slot = entry.getValue();

            int newRingIndex = calcRingIndex(ring);
            ringIndex.put(ring, newRingIndex);

            Vector2 newPos = new Vector2(firstRingBounds.x + newRingIndex * RING_OFFSET, firstRingBounds.y);
            slot.moveTo(newPos);
        }
    }

    private int calcRingIndex(AbstractRing ring) {
        List<Map.Entry<AbstractRing, DragableSlot>> sorted = getEntriesSortedByX();
        for (Map.Entry<AbstractRing, DragableSlot> entry : sorted) {
            if (entry.getKey() == ring) return sorted.indexOf(entry);
        }
        return -1;
    }

    private List<Map.Entry<AbstractRing, DragableSlot>> getEntriesSortedByX() {
        List<Map.Entry<AbstractRing, DragableSlot>> sorted = new ArrayList<>(rings.entrySet());
        sorted.sort(Comparator.comparingDouble(entry -> entry.getValue().getRenderPos(new Vector2()).x));
        return sorted;
    }

}
