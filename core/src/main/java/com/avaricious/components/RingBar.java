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
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.FontDrawing;
import com.avaricious.utility.Observable;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RingBar extends Observable<List<? extends AbstractRing>> {

    private static RingBar instance;

    public static RingBar I() {
        return instance == null ? instance = new RingBar() : instance;
    }

    private final TextureRegion ringSlot = Assets.I().get(AssetKey.RING_SLOT);

    private final Rectangle firstRingBounds = new Rectangle(0.25f, 5.925f, 1.1f, 1.1f);
    private final float RING_OFFSET = 1.25f;

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
        rings.stream().map(AbstractRing::getBody).forEach(ring -> ring.update(delta));

        if (pressed && !wasPressed) {
            Optional<AbstractRing> r = rings.stream()
                .filter(ring -> ring.getBody().getBounds().contains(mouse))
                .findFirst();

            if (r.isPresent()) onRingTouchDown(r.get(), mouse);
            else deselectRing(true);

        }

        if (pressed && touchingRing != null) {
            onRingTouching(touchingRing, mouse);
        }

        if (!pressed && wasPressed && touchingRing != null) {
            onRingTouchReleased(touchingRing, mouse);
        }

        if (touchingRing != null || selectedRing != null) {
            AbstractRing ring = touchingRing == null ? selectedRing : touchingRing;
            Vector2 ringRenderPos = ring.getBody().getRenderPos(new Vector2());
            PopupManager.I().updateTooltip(
                new Vector2(ringRenderPos.x - 2f, ringRenderPos.y + ring.getTooltipYOffset()),
                true
            );
        }
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
        Pencil.I().addDrawing(new TextureDrawing(ringSlot,
            new Rectangle(0.1f, 5.65f, 162 / 25f, 40 / 25f),
            ZIndex.RING_BAR, Assets.I().shadowColor()));
        rings.forEach(this::drawRing);

        Vector2 ringsHoldingPos = new Vector2(4.9f * 100, 5.475f * 100f);
        ringsHoldingTxt.setText(Assets.I().getSmallFont(), rings.size() + " / 5", Color.WHITE, 200f, Align.top | Align.center, true);
        Pencil.I().addDrawing(new FontDrawing(Assets.I().getSmallFont(), ringsHoldingTxt, ringsHoldingPos, ZIndex.RING_BAR));
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
            new Rectangle(position.x, position.y - (isTouching ? 0.2f : 0.1f),
                bounds.width, bounds.height
            ), scale, rotation, isTouching ? ZIndex.RING_BAR_DRAGGING : ZIndex.RING_BAR, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(0.25f, alpha))));
        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().get(ring.keySet().getTextureKey()),
            new Rectangle(position.x, position.y, bounds.width, bounds.height),
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
        return rings.stream()
            .filter(ringClass::isInstance)
            .map(ringClass::cast)
            .findFirst().orElse(null);
    }


    public <T> List<T> getRingsOfType(Class<T> type) {
        return rings.stream()
            .filter(type::isInstance)
            .map(type::cast)
            .collect(Collectors.toList());
    }

    public boolean ringOwned(Class<? extends AbstractRing> ringClass) {
        return rings.stream()
            .anyMatch(ringClass::isInstance);
    }

    public List<AbstractRing> getRings() {
        return rings;
    }

    public int size() {
        return rings.size();
    }

    private void updateRingIndexes() {
        rings.forEach(ring -> {
            int newRingIndex = calcRingIndex(ring);
            ringIndex.put(ring, newRingIndex);

            Vector2 newPos = new Vector2(firstRingBounds.x + newRingIndex * RING_OFFSET, firstRingBounds.y);
            ring.getBody().moveTo(newPos);
        });
    }

    private int calcRingIndex(AbstractRing ring) {
        List<AbstractRing> sorted = getEntriesSortedByX();
        return IntStream.range(0, sorted.size())
            .filter(i -> sorted.get(i) == ring)
            .findFirst()
            .orElse(-1);
    }

    private List<AbstractRing> getEntriesSortedByX() {
        List<AbstractRing> sorted = new ArrayList<>(rings);
        sorted.sort(Comparator.comparingDouble(ring -> ring.getBody().getRenderPos(new Vector2()).x));
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
        rings.forEach(this::addRing);
    }

    @Override
    protected List<? extends AbstractRing> snapshot() {
        return rings;
    }
}
