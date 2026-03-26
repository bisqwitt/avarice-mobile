package com.avaricious.components.roundInfoPanel;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RoundInfoPanel {

    private final ScoreDisplay scoreDisplay = ScoreDisplay.I();
    private final SymbolValueDisplay symbolValueDisplay = new SymbolValueDisplay();

    private final Rectangle panelBoundsFolded = new Rectangle(0, 14.7f, 9f, 9f);
    private final Rectangle panelBoundsUnfolded = new Rectangle(0f, 9f, 9f, 9f);
    private final Rectangle currentPanelBounds = new Rectangle(panelBoundsFolded);

    private Vector2 mouseTouchdownLocation = null;
    private float panelYOnMouseTouchdown = -1;

    private float targetPanelY = panelBoundsFolded.y;

    private float unfoldAmount = 0f;

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching) {
        if (touching && !wasTouching) {
            if (currentPanelBounds.contains(mouse)) {
                mouseTouchdownLocation = new Vector2(mouse);
                panelYOnMouseTouchdown = currentPanelBounds.y;
            }
        }

        if (touching && mouseTouchdownLocation != null) {
            float panelTargetY = panelYOnMouseTouchdown - (mouseTouchdownLocation.y - mouse.y);

            currentPanelBounds.y = Math.max(
                panelBoundsUnfolded.y,
                Math.min(panelBoundsFolded.y, panelTargetY)
            );
        }

        if (!touching && wasTouching) {
            targetPanelY = Math.abs(currentPanelBounds.y - panelBoundsUnfolded.y) < Math.abs(currentPanelBounds.y - panelBoundsFolded.y)
                ? panelBoundsUnfolded.y : panelBoundsFolded.y;

            mouseTouchdownLocation = null;
        }
    }

    public void update(float delta) {
        updateUnfoldAmount();

        if (mouseTouchdownLocation == null) {
            float panelMoveSpeed = 20f;
            currentPanelBounds.y = MathUtils.lerp(
                currentPanelBounds.y,
                targetPanelY,
                panelMoveSpeed * delta
            );

            if (Math.abs(currentPanelBounds.y - targetPanelY) < 0.01f) {
                currentPanelBounds.y = targetPanelY;
            }
        }
    }

    public void draw(float delta) {
        update(delta);

        // For Camera Shake
        Rectangle drawBounds = new Rectangle(currentPanelBounds);
        drawBounds.x -= 3;
        drawBounds.width += 6;
        drawBounds.height += 3;

//        Pencil.I().addDrawing(new TextureDrawing(borderWhite,
//            new Rectangle(drawBounds.x, drawBounds.y, drawBounds.width, 0.05f),
//            unfoldAmount == 0 ? ZIndex.WARP_BACKGROUND : ZIndex.ROUND_INFO_PANEL_UNFOLDED));

//        targetScoreDisplay.draw(delta, unfoldAmount);
        scoreDisplay.draw(delta, unfoldAmount);

//        if(unfoldAmount != 0) {
//            symbolValueDisplay.draw(delta);
//        }
    }

    public ScoreDisplay getScoreDisplay() {
        return scoreDisplay;
    }

    private void updateUnfoldAmount() {
        float range = panelBoundsFolded.y - panelBoundsUnfolded.y;
        unfoldAmount = MathUtils.clamp(
            (panelBoundsFolded.y - currentPanelBounds.y) / range,
            0f,
            1f
        );
    }
}
