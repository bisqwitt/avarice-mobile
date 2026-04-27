package com.avaricious.components;

import com.avaricious.effects.IdleFloatEffect;
import com.avaricious.effects.IdleSwayEffect;
import com.avaricious.effects.PulseEffect;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class DigitalNumber {

    protected final List<TextureRegion> numberTextures = new ArrayList<>();
    protected final List<TextureRegion> numberShadowTextures = new ArrayList<>();
    protected final TextureRegion dotSymbol = Assets.I().get(AssetKey.DOT_SYMBOL);

    protected final Color color;
    protected final Rectangle firstDigitBounds;
    protected final float offset;

    private ZIndex zIndex = ZIndex.DIGITAL_NUMBER;

    private float score;
    private boolean asDecimal = false;

    private final PulseEffect pulseEffect = new PulseEffect();
    private final IdleFloatEffect floatEffect = new IdleFloatEffect();
    private final IdleSwayEffect swayEffect = new IdleSwayEffect(1.2f, 0.4f);

    public DigitalNumber(int initialScore, Color color, Rectangle firstDigitBounds, float offset) {
        setScore(initialScore);
        this.color = color;
        this.firstDigitBounds = firstDigitBounds;
        this.offset = offset;

        updateDigitalNumbers(initialScore);
    }

    public DigitalNumber(int initialScore, Color color, int setLength, Rectangle firstDigitBounds, float offset) {
        score = initialScore;
        this.color = color;
        this.firstDigitBounds = firstDigitBounds;
        this.offset = offset;

        for (int i = 0; i < setLength; i++) {
            numberTextures.add(Assets.I().getDigitalNumber(0));
            numberShadowTextures.add(Assets.I().getDigitalNumberShadow(0));
        }
        updateDigitalNumbers(score);
    }

    public void draw(float delta) {
        draw(delta, pulseEffect.getScale(), calcScale() + calcRotation());
    }

    public void draw(float delta, float scale, float rotation) {
        floatEffect.update(delta);
        swayEffect.update(delta);
        pulseEffect.update(delta);

        float numberY = calcNumberY();
        int decimalPlaces = countDecimalPlaces(score);
        int intDigitCount = numberTextures.size() - decimalPlaces;
        float dotOffset = offset * 0.5f;

        for (int i = 0; i < numberTextures.size(); i++) {
            // Decimal digits shift right by dotOffset (half) instead of a full offset
            float extraOffset = (asDecimal && decimalPlaces > 0 && i >= intDigitCount) ? dotOffset : 0;

            Pencil.I().addDrawing(new TextureDrawing(
                numberShadowTextures.get(i),
                new Rectangle(firstDigitBounds.x + (i * offset) + extraOffset, numberY - 0.1f, firstDigitBounds.width, firstDigitBounds.height),
                scale, rotation, getZIndex(), Assets.I().shadowColor()
            ));
            Pencil.I().addDrawing(new TextureDrawing(
                numberTextures.get(i),
                new Rectangle(firstDigitBounds.x + (i * offset) + extraOffset, numberY, firstDigitBounds.width, firstDigitBounds.height),
                scale, rotation, getZIndex(), color
            ));
        }

        if (asDecimal && decimalPlaces > 0) {
            // Dot sits at half-offset after the last integer digit
            float dotX = firstDigitBounds.x + (intDigitCount * offset);
            Pencil.I().addDrawing(new TextureDrawing(
                dotSymbol,
                new Rectangle(dotX, numberY, firstDigitBounds.width, firstDigitBounds.height),
                scale, rotation, getZIndex(), color
            ));
        }
    }

    private void updateDigitalNumbers(float score) {
        Assets assetManager = Assets.I();
        int decimalPlaces = countDecimalPlaces(score);

        if (decimalPlaces > 0) {
            int scaledDecimals = Math.round((score % 1) * (int) Math.pow(10, decimalPlaces));
            for (int i = numberTextures.size() - 1; i >= numberTextures.size() - decimalPlaces; i--) {
                int digit = scaledDecimals % 10;
                numberTextures.set(i, assetManager.getDigitalNumber(digit));
                numberShadowTextures.set(i, assetManager.getDigitalNumberShadow(digit));
                scaledDecimals /= 10;
            }

            int intPart = (int) score;
            for (int i = numberTextures.size() - decimalPlaces - 1; i >= 0; i--) {
                int digit = intPart % 10;
                numberTextures.set(i, assetManager.getDigitalNumber(digit));
                numberShadowTextures.set(i, assetManager.getDigitalNumberShadow(digit));
                intPart /= 10;
            }
        } else {
            int tempScore = (int) score;
            for (int i = numberTextures.size() - 1; i >= 0; i--) {
                int digit = tempScore % 10;
                numberTextures.set(i, assetManager.getDigitalNumber(digit));
                numberShadowTextures.set(i, assetManager.getDigitalNumberShadow(digit));
                tempScore /= 10;
            }
        }
    }

    public void setScore(float score) {
        if (score < 0) score = 0;
        this.score = score;

        int decimalPlaces = countDecimalPlaces(score);
        int intDigits = (int) score == 0 ? 1 : (int) Math.log10(score) + 1;
        int totalDigits = intDigits + decimalPlaces;

        while (numberTextures.size() < totalDigits) {
            numberTextures.add(Assets.I().getDigitalNumber(0));
            numberShadowTextures.add(Assets.I().getDigitalNumberShadow(0));
        }
        while (numberTextures.size() > totalDigits) {
            numberTextures.remove(numberTextures.size() - 1);
            numberShadowTextures.remove(numberShadowTextures.size() - 1);
        }

        updateDigitalNumbers(score);
        pulseEffect.pulse();
    }

    private int countDecimalPlaces(float score) {
        if (!asDecimal) return 0;
        String text = Float.toString(score);
        int dotIndex = text.indexOf('.');
        if (dotIndex == -1) return 0;

        String decimals = text.substring(dotIndex + 1);
        decimals = decimals.replaceAll("0+$", "");
        return decimals.length();
    }

    public float getWidth() {
        int decimalPlaces = countDecimalPlaces(score);
        float dotOffset = offset * 0.5f;
        float extraWidth = (asDecimal && decimalPlaces > 0) ? dotOffset : 0;
        return ((numberTextures.size() - 1) * offset) + extraWidth + firstDigitBounds.width;
    }

    public float getScore() {
        return score;
    }

    public Rectangle getFirstDigitBounds() {
        return firstDigitBounds;
    }

    public float calcNumberY() {
        return firstDigitBounds.y + floatEffect.getYOffset();
    }

    public float calcScale() {
        return pulseEffect.getScale();
    }

    public float calcRotation() {
        return pulseEffect.getRotation() + swayEffect.getRotation();
    }

    public float getRotation() {
        return swayEffect.getRotation();
    }

    protected ZIndex getZIndex() {
        return zIndex;
    }

    public void setZIndex(ZIndex zIndex) {
        this.zIndex = zIndex;
    }

    public DigitalNumber setAsDecimal() {
        asDecimal = true;
        return this;
    }
}
