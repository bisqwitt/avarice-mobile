package com.avaricious.components.popups;

import com.avaricious.upgrades.Upgrade;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PopupManager {

    private static PopupManager instance;

    public static PopupManager I() {
        return instance == null ? instance = new PopupManager() : instance;
    }

    private PopupManager() {
    }

    private final List<NumberPopup> numberPopups = new ArrayList<>();
    private final List<StatisticPopup> statisticPopups = new ArrayList<>();
    private final List<AbstractTextPopup> textPopups = new ArrayList<>();

    private TooltipPopup tooltipPopup;
    private TooltipPopup killingTooltip;
    private ApplyPopup applyPopup;
    private DiscardPopup discardPopup;

    public TooltipPopup createTooltip(Upgrade upgrade, Vector2 pos) {
        if (upgrade == null) return null;
        if (tooltipPopup != null && tooltipPopup.getUpgrade() == upgrade) return tooltipPopup;
        return tooltipPopup = new TooltipPopup(upgrade, pos);
    }

    public TooltipPopup createTooltip(Upgrade upgrade, Vector2 pos, ZIndex layer) {
        if (upgrade == null) return null;
        return tooltipPopup = new TooltipPopup(upgrade, pos, layer);
    }

    public void updateTooltip(Vector2 pos, boolean visible) {
        if (tooltipPopup == null) return;
        tooltipPopup.update(pos, visible);
    }

    public void updateTooltip(Vector2 pos, boolean visible, boolean showApplyBox, boolean showDiscardBox) {
        if (tooltipPopup == null) return;
        if (!showApplyBox && !showDiscardBox) tooltipPopup.update(pos, visible);
        if (applyPopup != null) applyPopup.update(pos);
        if (discardPopup != null) discardPopup.update(pos);

        if (showApplyBox && applyPopup == null) {
            tooltipPopup.setVisible(false);
            applyPopup = new ApplyPopup(pos);
        }

        if (!showApplyBox && (applyPopup != null && !applyPopup.isKilled())) {
            applyPopup.kill(() -> applyPopup = null);
            tooltipPopup.setVisible(true);
        }

        if (showDiscardBox && discardPopup == null) {
            tooltipPopup.setVisible(false);
            discardPopup = new DiscardPopup(pos);
        }

        if (!showDiscardBox && (discardPopup != null && !discardPopup.isKilled())) {
            discardPopup.kill(() -> discardPopup = null);
            tooltipPopup.setVisible(true);
        }
    }

    public void killTooltip(TooltipPopup popup) {
        if(tooltipPopup != null && tooltipPopup == popup) tooltipPopup = null;
        if (applyPopup != null) applyPopup.kill(() -> applyPopup = null);
        if (discardPopup != null) discardPopup.kill(() -> discardPopup = null);
    }

    public void spawnNumber(NumberPopup numberPopup) {
        numberPopups.add(numberPopup);
    }

    public void spawnNumber(int number, Color color, float x, float y, boolean manualHold) {
        spawnNumber(new NumberPopup(number, color, x, y, false, manualHold));
    }

    public void spawnTextPopup(AbstractTextPopup textPopup) {
        textPopups.add(textPopup);
    }

    public void releaseHoldingNumbers() {
        for (NumberPopup numberPopup : numberPopups) {
            if (numberPopup.isManualHold()) numberPopup.release();
        }
    }

    public NumberPopup spawnPercentage(int number, Color color, float x, float y) {
        NumberPopup popup = new NumberPopup(number, color, x, y, true, false);
        numberPopups.add(popup);
        return popup;
    }

    public void spawnStatisticHit(TextureRegion texture, float x, float y) {
        statisticPopups.add(new StatisticPopup(texture, x, y));
    }

    public void draw(SpriteBatch batch, float delta) {
        drawPopups(numberPopups, delta);
        drawPopups(statisticPopups, delta);
        drawPopups(textPopups, delta);

        if (tooltipPopup != null) tooltipPopup.render(batch, delta);
        if(killingTooltip != null) killingTooltip.render(batch, delta);
        if (applyPopup != null) applyPopup.draw(delta);
        if (discardPopup != null) discardPopup.draw(delta);
    }

    private void drawPopups(List<? extends IPopup> popups, float delta) {
        popups.forEach(popup -> {
            popup.update(delta);
            popup.draw();
        });
        popups.removeIf(IPopup::isFinished);
    }
}

