package com.avaricious.components.roundInfoPanel;

import com.avaricious.CreditScoreWithText;
import com.avaricious.RoundsManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.texts.CreditsText;
import com.avaricious.components.texts.RoundText;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RoundInfoPanel {

    private final ScoreDisplay scoreDisplay = ScoreDisplay.I();

    private final Rectangle panelBoundsFolded = new Rectangle(0, 14.7f, 9f, 9f);
    private final Rectangle panelBoundsUnfolded = new Rectangle(0f, 9f, 9f, 9f);
    private final Rectangle currentPanelBounds = new Rectangle(panelBoundsFolded);

    private final RoundText roundText = new RoundText(new Vector2(5.25f, 19.1f), 30f, 0.05f, ZIndex.PATTERN_DISPLAY);
    private final DigitalNumber currentRoundNumber = new DigitalNumber(1, new Color(1f, 1f, 1f, 1f), 1,
        new Rectangle(5.75f, 18.35f, 7 / 23f, 11 / 23f), 0.7f);
    private final CreditsText creditsText = new CreditsText(new Vector2(7f, 19.1f), 30f, 0.05f, ZIndex.PATTERN_DISPLAY);
    private final CreditScoreWithText creditScore = new CreditScoreWithText(new Vector2(4.95f, 18.35f), 23f, 0.4f);

    private final TextureRegion black = Assets.I().get(AssetKey.BLACK_PIXEL);
    private final TextureRegion white = Assets.I().get(AssetKey.WHITE_PIXEL);

    private Vector2 mouseTouchdownLocation = null;
    private float panelYOnMouseTouchdown = -1;

    private float targetPanelY = panelBoundsFolded.y;

    private float unfoldAmount = 0f;

    public RoundInfoPanel() {
        RoundsManager.I().onChange(currentRoundNumber::setValue);
    }

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

        roundText.draw(delta);
        currentRoundNumber.draw(delta);
        creditsText.draw(delta);
        creditScore.draw(delta);

        Pencil.I().addDrawing(new TextureDrawing(black,
            0, 18f, 9f, 5f, ZIndex.SCORE_DISPLAY, Assets.I().shadowColor()));
        Pencil.I().addDrawing(new TextureDrawing(white,
            0, 18f, 9f, 0.05f, ZIndex.SCORE_DISPLAY));

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
