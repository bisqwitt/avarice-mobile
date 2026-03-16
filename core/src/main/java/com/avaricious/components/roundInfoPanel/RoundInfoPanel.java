package com.avaricious.components.roundInfoPanel;

import com.avaricious.audio.AudioManager;
import com.avaricious.screens.ScreenManager;
import com.avaricious.screens.SlotScreen;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RoundInfoPanel {

    private final TextureRegion backgroundTexture = Assets.I().get(AssetKey.BACKGROUND_PIXEL);

    private final TargetScoreDisplay targetScoreDisplay = new TargetScoreDisplay();
    private final ScoreDisplay scoreDisplay = ScoreDisplay.I();

    private final Rectangle panelBoundsFolded = new Rectangle(0, 14.5f, 9f, 9f);
    private final Rectangle panelBoundsUnfolded = new Rectangle(0f, 9f, 9f, 9f);
    private final Rectangle currentPanelBounds = new Rectangle(panelBoundsFolded);

    private Vector2 mouseTouchdownLocation = null;

    private float unfoldAmount = 0f;

    public RoundInfoPanel() {
        targetScoreDisplay.setOnInternalScoreDisplayed(() -> {
            AudioManager.I().endPayout();
            if (targetScoreDisplay.targetScoreReached())
                ScreenManager.I().getScreen(SlotScreen.class).onTargetScoreReached();
        });
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching) {
        if (touching && !wasTouching) {
            if (currentPanelBounds.contains(mouse)) {
                mouseTouchdownLocation = new Vector2(mouse);
            }
        }

        if (touching && mouseTouchdownLocation != null) {
            float panelTargetY = panelBoundsFolded.y - (mouseTouchdownLocation.y - mouse.y);

            currentPanelBounds.y = Math.max(
                panelBoundsUnfolded.y,
                Math.min(panelBoundsFolded.y, panelTargetY)
            );
        }

        if (!touching && wasTouching) {
            float panelTargetY = Math.abs(currentPanelBounds.y - panelBoundsUnfolded.y) < Math.abs(currentPanelBounds.y - panelBoundsFolded.y)
                ? panelBoundsUnfolded.y : panelBoundsFolded.y;
            currentPanelBounds.y = panelTargetY;
            mouseTouchdownLocation = null;
        }
    }

    public void draw(float delta) {
        // For Camera Shake
        Rectangle drawBounds = new Rectangle(currentPanelBounds);
        drawBounds.x -= 3;
        drawBounds.width += 6;
        drawBounds.height += 3;

        Pencil.I().addDrawing(new TextureDrawing(backgroundTexture,
            drawBounds, ZIndex.WARP_BACKGROUND));
        targetScoreDisplay.draw(delta);
        scoreDisplay.draw(delta);
    }

    public TargetScoreDisplay getTargetScoreDisplay() {
        return targetScoreDisplay;
    }

    public ScoreDisplay getScoreDisplay() {
        return scoreDisplay;
    }
}
