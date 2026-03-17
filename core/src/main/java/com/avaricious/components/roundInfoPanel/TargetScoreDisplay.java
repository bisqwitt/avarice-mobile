package com.avaricious.components.roundInfoPanel;

import com.avaricious.RoundsManager;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.components.slot.SlotMachine;
import com.avaricious.screens.ScreenManager;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.FontDrawing;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class TargetScoreDisplay {

    private final float DIGIT_WIDTH = 7 / 13f;
    private final float DIGIT_HEIGHT = 11 / 13f;

    private final float FOLDED_Y = 16.5f;

    private final float ROUND_TXT_X = 1f;
    private final float CURRENT_ROUND_X = ROUND_TXT_X + 3.25f; // 4.25f
    private final float COLON_X = CURRENT_ROUND_X + 0.6f; // 4.75
    private final float TARGET_SCORE_X = COLON_X + 1f; // 5.5

    private final DigitalNumber currentAnteNumber;
    private final DigitalNumber currentRoundNumber;
    private final DigitalNumber targetScoreNumber;

    private final TextureRegion anteTxt = Assets.I().get(AssetKey.ANTE_TXT);
    private final TextureRegion anteTxtShadow = Assets.I().get(AssetKey.ANTE_TXT_SHADOW);

    private final TextureRegion roundTxt = Assets.I().get(AssetKey.ROUND_TXT);
    private final TextureRegion roundTxtShadow = Assets.I().get(AssetKey.ROUND_TXT_SHADOW);

    private final TextureRegion bossTxt = Assets.I().get(AssetKey.BOSS_TXT);
    private final TextureRegion bossTxtShadow = Assets.I().get(AssetKey.BOSS_TXT_SHADOW);
    private final GlyphLayout bossDescription = new GlyphLayout();

    private final TextureRegion colon = Assets.I().get(AssetKey.COLON);

    public TargetScoreDisplay() {
        currentAnteNumber = new DigitalNumber(1, Assets.I().lightColor(),
            new Rectangle(5.5f, 16.6f, DIGIT_WIDTH, DIGIT_HEIGHT), 0.75f);
        currentRoundNumber = new DigitalNumber(RoundsManager.I().getCurrentRound(), Assets.I().lightColor(),
            new Rectangle(CURRENT_ROUND_X, FOLDED_Y, DIGIT_WIDTH, DIGIT_HEIGHT), 0.9f);
        targetScoreNumber = new DigitalNumber(RoundsManager.I().getCurrentTargetScore(), Assets.I().lightColor(),
            new Rectangle(TARGET_SCORE_X, FOLDED_Y, DIGIT_WIDTH, DIGIT_HEIGHT), 0.75f);
    }

    public void draw(float delta, float unfoldAmount) {
        float t = MathUtils.clamp(unfoldAmount, 0f, 1f);
        float smoothT = Interpolation.smoother.apply(t);

        boolean folded = t == 0f;
        ZIndex zIndex = folded ? ZIndex.SCORE_DISPLAY : ZIndex.ROUND_INFO_PANEL_UNFOLDED;

        currentAnteNumber.setZIndex(zIndex);
        currentRoundNumber.setZIndex(zIndex);
        targetScoreNumber.setZIndex(zIndex);

        // ROUND / TARGET row moves upward
        float mainY = MathUtils.lerp(FOLDED_Y, FOLDED_Y - 1f, smoothT);
        currentRoundNumber.getBounds().y = mainY;
        targetScoreNumber.getBounds().y = mainY;

        Pencil.I().addDrawing(new TextureDrawing(
            RoundsManager.I().isBossRound() ? bossTxtShadow : roundTxtShadow,
            new Rectangle(
                ROUND_TXT_X,
                currentRoundNumber.calcHoverY() - 0.1f,
                37f / 13f,
                DIGIT_HEIGHT
            ),
            ZIndex.ROUND_INFO_PANEL_UNFOLDED,
            Assets.I().shadowColor()
        ));

        Pencil.I().addDrawing(new TextureDrawing(
            RoundsManager.I().isBossRound() ? bossTxt : roundTxt,
            new Rectangle(
                ROUND_TXT_X,
                currentRoundNumber.calcHoverY(),
                37f / 13f,
                DIGIT_HEIGHT
            ),
            ZIndex.ROUND_INFO_PANEL_UNFOLDED
        ));

        currentRoundNumber.draw(delta);

        Pencil.I().addDrawing(new TextureDrawing(
            colon,
            new Rectangle(
                COLON_X,
                currentRoundNumber.calcHoverY(),
                DIGIT_WIDTH,
                DIGIT_HEIGHT
            ),
            ZIndex.ROUND_INFO_PANEL_UNFOLDED
        ));

        targetScoreNumber.draw(delta);

        // ANTE comes down from above and stays ABOVE the round row
        float anteStart = 0.15f;
        float anteProgress = MathUtils.clamp((t - anteStart) / (1f - anteStart), 0f, 1f);
        float anteSmoothT = Interpolation.smoother.apply(anteProgress);

        // final resting position for ANTE (above ROUND)
        float anteFinalY = mainY + 1.3f;

        // start even higher, then come down to anteFinalY
        float anteStartY = anteFinalY + 0.25f;
        float anteY = MathUtils.lerp(anteStartY, anteFinalY, anteSmoothT);

        currentAnteNumber.getBounds().y = anteY;

        if (RoundsManager.I().isBossRound() && SlotMachine.I().isStale()) {
            BitmapFont font = Assets.I().getMediumFont();
            bossDescription.setText(font, RoundsManager.I().getBoss().description(),
                Color.WHITE, 1000f, Align.top | Align.center, true);

            float screenCenter = (ScreenManager.getViewport().getWorldWidth() / 2f) * 100;
            float textX = screenCenter - 1000f / 2f;
            float textY = (currentRoundNumber.calcHoverY() - 2.5f) * 100;

            Pencil.I().addDrawing(new FontDrawing(font, bossDescription,
                new Vector2(textX, textY),
                ZIndex.ROUND_INFO_PANEL_UNFOLDED));
        }

        if (anteProgress > 0f) {
            Pencil.I().addDrawing(new TextureDrawing(
                anteTxtShadow,
                new Rectangle(
                    2.5f,
                    currentAnteNumber.calcHoverY() - 0.1f,
                    31f / 13f,
                    11f / 13f
                ),
                ZIndex.ROUND_INFO_PANEL_UNFOLDED,
                Assets.I().shadowColor()
            ));

            Pencil.I().addDrawing(new TextureDrawing(
                anteTxt,
                new Rectangle(
                    2.5f,
                    currentAnteNumber.calcHoverY(),
                    31f / 13f,
                    11f / 13f
                ),
                ZIndex.ROUND_INFO_PANEL_UNFOLDED
            ));

            currentAnteNumber.draw(delta);
        }
    }

    public void addToScore(int amount) {
        AudioManager.I().startPayout();
        targetScoreNumber.setScore(Math.max(targetScoreNumber.getScore() - amount, 0));
    }

    public void resetScore() {
        targetScoreNumber.setScore(RoundsManager.I().getCurrentTargetScore());
        currentRoundNumber.setScore(RoundsManager.I().getCurrentRound());
    }

    public boolean targetScoreReached() {
        return targetScoreNumber.getScore() == 0;
    }

    public void setOnInternalScoreDisplayed(Runnable onInternalScoreDisplayed) {
        targetScoreNumber.setOnInternalScoreDisplayed(onInternalScoreDisplayed);
    }

}
