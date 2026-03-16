package com.avaricious.components.roundInfoPanel;

import com.avaricious.RoundsManager;
import com.avaricious.audio.AudioManager;
import com.avaricious.components.DigitalNumber;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class TargetScoreDisplay {

    private final float DIGIT_WIDTH = 7 / 13f;
    private final float DIGIT_HEIGHT = 11 / 13f;

    private final float FOLDED_Y = 16.4f;

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
        boolean folded = unfoldAmount == 0;
        updateZIndex(folded);
        updateY(folded);

        Pencil.I().addDrawing(new TextureDrawing(roundTxtShadow,
            new Rectangle(ROUND_TXT_X, currentRoundNumber.calcHoverY() - 0.1f, 37 / 13f, DIGIT_HEIGHT),
            ZIndex.ROUND_INFO_PANEL_UNFOLDED, Assets.I().shadowColor()));
        Pencil.I().addDrawing(new TextureDrawing(roundTxt,
            new Rectangle(ROUND_TXT_X, currentRoundNumber.calcHoverY(), 37 / 13f, DIGIT_HEIGHT), ZIndex.ROUND_INFO_PANEL_UNFOLDED));
        currentRoundNumber.draw(delta);
        Pencil.I().addDrawing(new TextureDrawing(colon,
            new Rectangle(COLON_X, currentRoundNumber.calcHoverY(), DIGIT_WIDTH, DIGIT_HEIGHT), ZIndex.ROUND_INFO_PANEL_UNFOLDED));
        targetScoreNumber.draw(delta);

        if(!folded) {
            Pencil.I().addDrawing(new TextureDrawing(anteTxtShadow,
                new Rectangle(2.5f, currentAnteNumber.calcHoverY() - 0.1f, 31 / 13f, 11 / 13f),
                ZIndex.ROUND_INFO_PANEL_UNFOLDED, Assets.I().shadowColor()));
            Pencil.I().addDrawing(new TextureDrawing(anteTxt,
                new Rectangle(2.5f, currentAnteNumber.calcHoverY(), 31 / 13f, 11 / 13f),
                ZIndex.ROUND_INFO_PANEL_UNFOLDED));
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

    private void updateZIndex(boolean folded) {
        ZIndex zIndex = folded ? ZIndex.SCORE_DISPLAY : ZIndex.ROUND_INFO_PANEL_UNFOLDED;
        currentAnteNumber.setZIndex(zIndex);
        currentRoundNumber.setZIndex(zIndex);
        targetScoreNumber.setZIndex(zIndex);
    }

    private void updateY(boolean folded) {
        float y = folded ? FOLDED_Y : FOLDED_Y - 1f;
        currentRoundNumber.getBounds().y = y;
        targetScoreNumber.getBounds().y = y;
    }

}
