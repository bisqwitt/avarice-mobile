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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RingBar {

    private static RingBar instance;

    public static RingBar I() {
        return instance == null ? instance = new RingBar() : instance;
    }

    public final int MAX_RINGS = 5;

    private final Rectangle firstRingBounds = new Rectangle(0.35f, 7.25f, 1.25f, 1.25f);
    private final float RING_OFFSET = 1.75f;

    private final TextureRegion shadow = Assets.I().get(AssetKey.JOKER_CARD_SHADOW);

    private final List<AbstractRing> rings = new ArrayList<>();
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
        rings.stream().map(AbstractRing::getBody).forEach(ring -> ring.update(delta));

        if (pressed && !wasPressed) {
            rings.stream()
                .filter(ring -> ring.getBody().getBounds().contains(mouse))
                .findFirst()
                .ifPresent(ring -> onRingTouchDown(ring, mouse));
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
        ring.getBody().targetScale = 1.3f;
        ring.getBody().beginDrag(mouse.x, mouse.y, 0);

        PopupManager.I().createTooltip(ring, ring.getBody().getRenderPos(new Vector2()));
    }

    private void onRingTouching(AbstractRing ring, Vector2 mouse) {
        DragableSlot body = ring.getBody();
        Vector2 ringRenderPos = body.getRenderPos(new Vector2());

        body.dragTo(mouse.x, mouse.y, 0);
        PopupManager.I().updateTooltip(
            new Vector2(ringRenderPos.x - 2f, ringRenderPos.y + 1.7f),
            true
        );

        updateRingIndexes();
    }

    private void onRingTouchReleased(AbstractRing ring) {
        DragableSlot body = ring.getBody();
        body.endDrag(0);
        body.targetScale = 1f;
        touchingRing = null;
        PopupManager.I().killTooltip();
    }

    public void draw() {
        rings.forEach(ring -> ring.draw(ring == touchingRing));
    }

    public void addRing(AbstractRing ring) {
        int index = rings.size();
        Rectangle bounds = new Rectangle(firstRingBounds).setX(firstRingBounds.x + index * RING_OFFSET);
        ring.addBody(bounds);

        rings.add(ring);
        ringIndex.put(ring, index);
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

}
