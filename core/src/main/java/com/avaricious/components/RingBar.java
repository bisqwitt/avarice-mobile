package com.avaricious.components;

import com.avaricious.DevTools;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.popups.TooltipPopup;
import com.avaricious.components.slot.DragableBody;
import com.avaricious.items.upgrades.rings.AbstractRing;
import com.avaricious.items.upgrades.rings.DeptRing;
import com.avaricious.items.upgrades.rings.DoubleXpRing;
import com.avaricious.items.upgrades.rings.triggerable.multAdditions.MultiPerEmptyRingSlotRing;
import com.avaricious.items.upgrades.rings.triggerable.multAdditions.pattern.ThreeOfAKindMultiAdditionRing;
import com.avaricious.items.upgrades.rings.triggerable.pointAdditions.symbolValueStacker.CherryValueStackRing;
import com.avaricious.utility.*;
import com.avaricious.utility.Observable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.*;

public class RingBar extends Observable<List<? extends AbstractRing>> {

    private static RingBar instance;

    public static RingBar I() {
        return instance == null ? instance = new RingBar() : instance;
    }

    private final TextureRegion ringSlot = Assets.I().get(AssetKey.RING_SLOT);

    private final Rectangle firstRingBounds = new Rectangle(0.5f, 4f, 1.4f, 1.4f);
    private final float RING_OFFSET = 1.6f;

    private final List<AbstractRing> rings = new ArrayList<>();
    private final Map<AbstractRing, Integer> ringIndex = new HashMap<>();
    private final GlyphLayout ringsHoldingTxt = new GlyphLayout();

    private AbstractRing touchingRing = null;
    private AbstractRing selectedRing = null;

    private final Vector2 mouseTouchdownLocation = new Vector2();
    private TooltipPopup tooltipPopup = null;

    private RingBar() {
        notifyChanged(snapshot());
        if (DevTools.testRings()) {
            addRing(new DeptRing());
            addRing(new ThreeOfAKindMultiAdditionRing());
            addRing(new DoubleXpRing());
            addRing(new MultiPerEmptyRingSlotRing());
            addRing(new CherryValueStackRing());
        }
    }

    public void handleInput(Vector2 mouse, boolean pressed, boolean wasPressed, float delta) {
        Seq.of(rings).map(AbstractRing::getBody).forEach(ring -> ring.update(delta));
//
//        if (pressed && !wasPressed) {
//            AbstractRing r = Seq.of(rings)
//                .filter(ring -> ring.getBody().getBounds().contains(mouse))
//                .findFirstOrNull();
//
//            if (r != null) onRingTouchDown(r, mouse);
//            else deselectRing(true);
//
//        }
//
//        if (pressed && touchingRing != null) {
//            onRingTouching(touchingRing, mouse);
//        }
//
//        if (!pressed && wasPressed && touchingRing != null) {
//            onRingTouchReleased(touchingRing, mouse);
//        }
//
//        if (touchingRing != null || selectedRing != null) {
//            AbstractRing ring = touchingRing == null ? selectedRing : touchingRing;
//            Vector2 ringRenderPos = ring.getBody().getRenderPos(new Vector2());
//            PopupManager.I().updateTooltip(
//                new Vector2(ringRenderPos.x - 2f, ringRenderPos.y + ring.getTooltipYOffset()),
//                true
//            );
//        }
    }

    private void onRingTouchDown(AbstractRing ring, Vector2 mouse) {
        if (selectedRing != null && ring != selectedRing) deselectRing(false);
        touchingRing = ring;
        ring.getBody().targetScale = 1.3f;
        ring.getBody().setIdleEffectsEnabled(false);
        ring.getBody().beginDrag(mouse.x, mouse.y, 0);

        mouseTouchdownLocation.set(mouse);
        tooltipPopup = PopupManager.I().createTooltip(ring, ring.getBody().getRenderPos(new Vector2()));
    }

    private void onRingTouching(AbstractRing ring, Vector2 mouse) {
        DragableBody body = ring.getBody();

        body.dragTo(mouse.x, mouse.y, 0);
        updateRingIndexes();
    }

    private void onRingTouchReleased(AbstractRing ring, Vector2 mouse) {
        DragableBody body = ring.getBody();
        body.endDrag(0);
        boolean isClick = mouseTouchdownLocation.dst(mouse) <= 0.2f * 0.2f;
        if (isClick) {
            if (selectedRing == ring) deselectRing(true);
            else {
                if (selectedRing != null) deselectRing(false);
                selectedRing = ring;
            }
        } else selectedRing = ring;
        touchingRing = null;
    }

    public void draw() {
//        Pencil.I().addDrawing(new TextureDrawing(ringSlot,
//            0.1f, firstRingBounds.y - 0.275f, 162 / 25f, 40 / 25f,
//            ZIndex.RING_BAR, Assets.I().shadowColor()));
        Seq.of(rings).forEach(this::drawRing);

        Vector2 ringsHoldingPos = new Vector2(4.9f * 100, 5.9f * 100f);
        ringsHoldingTxt.setText(Assets.I().getSmallFont(), rings.size() + " / 5", Color.WHITE, 200f, Align.top | Align.center, true);
//        Pencil.I().addDrawing(new FontDrawing(Assets.I().getSmallFont(), ringsHoldingTxt, ringsHoldingPos, ZIndex.RING_BAR));
    }

    public void drawRing(AbstractRing ring) {
        DragableBody body = ring.getBody();
        Rectangle bounds = body.getBounds();
        boolean isTouching = ring == touchingRing;

        float scale = body.getScale();
        float rotation = body.getRotation();

        float alpha = body.getAlpha();
        Vector2 position = body.getRenderPos(new Vector2());

        Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().get(ring.keySet().getShadowKey()),
            position.x, position.y - (isTouching ? 0.2f : 0.1f),
            bounds.width, bounds.height,
            scale, rotation, isTouching ? ZIndex.RING_BAR_DRAGGING : ZIndex.RING_BAR, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(0.25f, alpha))));
        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().get(ring.keySet().getTextureKey()),
            position.x, position.y, bounds.width, bounds.height,
            scale, rotation, isTouching ? ZIndex.RING_BAR_DRAGGING : ZIndex.RING_BAR, new Color(1f, 1f, 1f, alpha)
        ));
    }

    public void addRing(AbstractRing ring) {
        int index = rings.size();
        Rectangle bounds = new Rectangle(firstRingBounds).setX(firstRingBounds.x + index * RING_OFFSET);
        ring.addBody(bounds);

        rings.add(ring);
        ringIndex.put(ring, index);

        notifyChanged(snapshot());
    }

    public <T extends AbstractRing> T getRingByClass(Class<T> ringClass) {
        return Seq.of(rings)
            .filter(ringClass::isInstance)
            .map(ringClass::cast)
            .findFirstOrNull();
    }


    public <T> List<T> getRingsOfType(Class<T> type) {
        return Seq.of(rings)
            .filter(type::isInstance)
            .map(type::cast)
            .toList();
    }

    public boolean ringOwned(Class<? extends AbstractRing> ringClass) {
        return Seq.of(rings)
            .anyMatch(ringClass::isInstance);
    }

    public List<AbstractRing> getRings() {
        return rings;
    }

    public int size() {
        return rings.size();
    }

    private void updateRingIndexes() {
        Seq.of(rings).forEach(ring -> {
            int newRingIndex = calcRingIndex(ring);
            ringIndex.put(ring, newRingIndex);

            Vector2 newPos = new Vector2(firstRingBounds.x + newRingIndex * RING_OFFSET, firstRingBounds.y);
            ring.getBody().moveTo(newPos);
        });
    }

    private int calcRingIndex(AbstractRing ring) {
        List<AbstractRing> sorted = getEntriesSortedByX();
        for(int i = 0; i < sorted.size() - 1; i++) {
            if(sorted.get(i) == ring) return i;
        }
        return -1;
    }

    private List<AbstractRing> getEntriesSortedByX() {
        List<AbstractRing> sorted = new ArrayList<>(rings);
        Collections.sort(sorted, new Comparator<AbstractRing>() {
            private final Vector2 tmpA = new Vector2();
            private final Vector2 tmpB = new Vector2();

            @Override
            public int compare(AbstractRing a, AbstractRing b) {
                float ax = a.getBody().getRenderPos(tmpA).x;
                float bx = b.getBody().getRenderPos(tmpB).x;

                if (ax < bx) return -1;
                if (ax > bx) return 1;
                return 0;
            }
        });
        return sorted;
    }

    public void deselectRing(boolean killTooltip) {
        if (selectedRing == null) return;
        selectedRing.getBody().targetScale = 1f;
        selectedRing.getBody().setIdleEffectsEnabled(true);
        selectedRing = null;
        if (killTooltip) {
            PopupManager.I().killTooltip(tooltipPopup);
            tooltipPopup = null;
        }
    }

    public void setRings(List<? extends AbstractRing> rings) {
        this.rings.clear();
        Seq.of(rings).forEach(this::addRing);
    }

    @Override
    protected List<? extends AbstractRing> snapshot() {
        return rings;
    }
}
