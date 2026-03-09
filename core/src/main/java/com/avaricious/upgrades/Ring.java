package com.avaricious.upgrades;

import com.avaricious.components.RingBar;
import com.avaricious.components.displays.PatternDisplay;
import com.avaricious.components.popups.PopupManager;
import com.avaricious.components.slot.DragableSlot;
import com.avaricious.upgrades.pointAdditions.PointsForEveryRingHit;
import com.avaricious.utility.Assets;
import com.avaricious.utility.RingAssetKeys;
import com.badlogic.gdx.graphics.Color;

public abstract class Ring extends Upgrade {

    public abstract RingAssetKeys keySet();

    @Override
    public String title() {
        return "Ring";
    }

    public abstract void hit();

    protected void pulse() {
        DragableSlot slot = RingBar.I().getSlotByRing(this);
        slot.pulse();
        slot.wobble();

        PointsForEveryRingHit pointsForEveryRingHit = RingBar.I().getRingByClass(PointsForEveryRingHit.class);
        if (pointsForEveryRingHit != null && !(this instanceof PointsForEveryRingHit))
            pointsForEveryRingHit.hit();
    }

    protected void numberPopup(Color popupColor, int value) {
        DragableSlot slot = RingBar.I().getSlotByRing(this);
        PopupManager.I().spawnNumber(value, popupColor,
            slot.getPos().x + 1.1f, slot.getPos().y + 1.1f,
            false);
    }

    protected void addToPattern(PatternDisplay.Type type, int value) {
        PatternDisplay.I().addTo(type, value);
        numberPopup(type == PatternDisplay.Type.POINTS ? Assets.I().blue() : Assets.I().red(), value);
    }
}
