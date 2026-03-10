package com.avaricious.upgrades.rings;

import com.avaricious.components.RingBar;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.upgrades.Upgrade;
import com.avaricious.upgrades.rings.triggerable.pointAdditions.PointsForEveryRingHit;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;
import com.badlogic.gdx.graphics.Color;

public abstract class AbstractRing extends Upgrade {

    public abstract RingAssetKeys keySet();

    @Override
    public String title() {
        return "Ring";
    }

    protected void pulse() {
        DragableSlot slot = RingBar.I().getSlotByRing(this);
        slot.pulse();
        slot.wobble();

        PointsForEveryRingHit pointsForEveryRingHit = RingBar.I().getRingByClass(PointsForEveryRingHit.class);
        if (pointsForEveryRingHit != null && !(this instanceof PointsForEveryRingHit))
            pointsForEveryRingHit.onRingHit();
    }

    protected void createNumberPopup(Color popupColor, int value) {
        DragableSlot slot = RingBar.I().getSlotByRing(this);
        PopupManager.I().spawnNumber(value, popupColor,
            slot.getPos().x + 1.1f, slot.getPos().y + 1.1f,
            false);
    }

    protected void addToPattern(PatternDisplay.Type type, int value) {
        PatternDisplay.I().addTo(type, value);
        createNumberPopup(type == PatternDisplay.Type.POINTS ? Assets.I().blue() : Assets.I().red(), value);
    }
}
