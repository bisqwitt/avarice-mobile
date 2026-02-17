package com.avaricious.components.slot;

import com.avaricious.DevTools;
import com.avaricious.Main;
import com.avaricious.TextureGlow;
import com.avaricious.components.slot.pattern.PatternFinder;
import com.avaricious.components.slot.pattern.PatternMatch;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SlotMachine {

    // --- Layout ---
    public static final int cols = 5;
    public static final int rows = 3;
    public static final float CELL_W = 1.5f;
    public static final float CELL_H = 1.5f;
    public static final float spacingX = 0.3f;
    public static final float spacingY = 0.3f;

    public static final float originX = 0.15f;
    public static final float originY = 8.75f;

    public static final Rectangle windowBounds = new Rectangle(0.05f, 8.25f, 8.85f, 6.1f);

    // Visual cells (for selection pulse/scale)
    private final Slot[][] grid = new Slot[cols][rows];

    private final TextureRegion slotBox = Assets.I().get(AssetKey.SLOT_BOX);
    private final TextureRegion slotBoxShadow = Assets.I().get(AssetKey.SLOT_BOX_SHADOW);

    private final TextureRegion darkGreenTexture = Assets.I().get(AssetKey.DARK_GREEN_PIXEL);
    private final TextureRegion blackGreenTexture = Assets.I().get(AssetKey.BLACK_GREEN_PIXEL);

    // Reels (one per column)
    private final List<Reel> reels = new ArrayList<>();

    private boolean runningResults = false;
    private float alpha = 1f;
    private float desiredAlpha = 1f;

    public SlotMachine() {
        // build visual cells
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (r == 0) grid[c][r] = new Slot(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 2 * (CELL_H + spacingY)
                ));
                if (r == 1) grid[c][r] = new Slot(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 1 * (CELL_H + spacingY)
                ));
                if (r == 2) grid[c][r] = new Slot(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 0 * (CELL_H + spacingY)
                ));
            }
        }

        // build basic reel strips (repeat symbol set to avoid short cycles)
        List<Symbol> baseStrip = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            baseStrip.add(Symbol.LEMON);
            if (!DevTools.onlyLemon) baseStrip.add(Symbol.CHERRY);
        }
        if (!DevTools.onlyLemon && !DevTools.lemonCherry) {
            for (int i = 0; i < 8; i++) {
                baseStrip.add(Symbol.CLOVER);
                baseStrip.add(Symbol.BELL);
            }
            for (int i = 0; i < 4; i++) {
                baseStrip.add(Symbol.IRON);
                baseStrip.add(Symbol.DIAMOND);
            }
            baseStrip.add(Symbol.SEVEN);
            baseStrip.add(Symbol.SEVEN);
        }

        for (int c = 0; c < cols; c++) {
            reels.add(new Reel(baseStrip, rows));
        }
    }

    // --- drawing ---
    public void draw(Main app, float delta) {
        SpriteBatch batch = app.getBatch();
        // update reel motion
        for (int c = 0; c < cols; c++) {
            reels.get(c).update(delta);
        }

        if (desiredAlpha != alpha) {
            float speed = 10f; // higher = faster convergence
            alpha = MathUtils.lerp(alpha, desiredAlpha, speed * delta);
        }

        // one big clip over the whole machine area
        Camera cam = app.getViewport().getCamera();
        cam.update();

        Rectangle area = getBounds(); // world-space
        area.setX(area.x - 0.3f);
        area.setWidth(area.width + 0.3f);
        area.setY(area.y - 0.3f);
        area.setHeight(area.height + 0.30f);

//        drawBorder(batch, area, blackGreenTexture, 0.25f);
//        drawBorder(batch, area, darkGreenTexture, 0.2f);
//        drawBorder(batch, area, blackGreenTexture, 0.075f);

        Rectangle scissors = new Rectangle();
        ScissorStack.calculateScissors(cam, batch.getTransformMatrix(), area, scissors);

        batch.flush();
        ScissorStack.pushScissors(scissors);

//        batch.draw(Assets.I().get(AssetKey.WHITE_PIXEL), 0f, 0f, 16f, 9f);

        drawBoxes(batch);
        TextureGlow.draw(batch, delta, TextureGlow.Type.SLOT);
        drawSymbols(batch, delta);

//        batch.setColor(1f, 1f, 1f, 0.25f);
//        batch.draw(Assets.I().getJokerCardShadow(), area.x - 2f, area.y + area.height - 0.5f, 15f, 3f);
//        batch.setColor(1f, 1f, 1f, 1f);

        batch.flush();
        ScissorStack.popScissors();
    }

    private void drawBorder(SpriteBatch batch, Rectangle area, TextureRegion texture, float size) {
        batch.draw(texture, area.x - size, area.y + area.height,
            area.width + size * 2, size);
        batch.draw(texture, area.x - size, area.y - size,
            size, area.height + size * 2);
        batch.draw(texture, area.x - size, area.y - size,
            area.width + size * 2, size);
        batch.draw(texture, area.x + area.width, area.y - size,
            size, area.height + size * 2);
    }

    private void drawBoxes(SpriteBatch batch) {
        final float stepX = (CELL_W + spacingX);
        final float stepY = (CELL_H + spacingY);
        final float topY = originY + (rows - 1) * stepY; // y of the top grid cell

        for (int c = 0; c < cols; c++) {
            Reel reel = reels.get(c);
            float frac = reel.frac(); // 0..1 progress toward next symbol
            float colX = originX + c * stepX;

            int extraAbove = 1;
            int extraBelow = 1;
            int drawFrom = -extraAbove;
            int drawTo = rows - 1 + extraBelow;

            for (int k = drawFrom; k <= drawTo; k++) {
                boolean isInGrid = (k >= 0 && k < rows);

                SymbolInstance symbolSlot = reel.slotAtRow(k);

                float drawX = colX;
                float drawY = topY - (k + frac) * stepY;

                boolean selected = false;
                float symbolsScale = 1f;
                float boxScale = 1f;

                // NEW: rotate around center using current wobble angle
                float rotation = isInGrid ? grid[c][k].wobbleAngleDeg() : 0f;

                float boxW = CELL_W + 0.2f * boxScale;
                float boxH = CELL_H + 0.2f * boxScale;
                float boxX = (drawX - (boxW - CELL_W) / 2f);
                float boxY = (drawY - (boxH - CELL_H) / 2f);

                batch.draw(
                    slotBox,
                    boxX, boxY,
                    boxW / 2f, boxH / 2f,
                    boxW, boxH,
                    1f, 1f,
                    rotation
                );
            }
        }
    }

    private void drawSymbols(SpriteBatch batch, float delta) {
        final float stepX = (CELL_W + spacingX);
        final float stepY = (CELL_H + spacingY);
        final float topY = originY + (rows - 1) * stepY; // y of the top grid cell

        for (int c = 0; c < cols; c++) {
            Reel reel = reels.get(c);
            float frac = reel.frac(); // 0..1 progress toward next symbol
            float colX = originX + c * stepX;

            int extraAbove = 1;
            int extraBelow = 1;
            int drawFrom = -extraAbove;
            int drawTo = rows - 1 + extraBelow;

            for (int k = drawFrom; k <= drawTo; k++) {
                boolean isInGrid = (k >= 0 && k < rows);

                SymbolInstance symbolSlot = reel.slotAtRow(k);

                float drawX = colX;
                float drawY = topY - (k + frac) * stepY;

                boolean selected = false;
                boolean hovered = false;
                float symbolsScale = 1f;
                float alpha = this.alpha;

                TextureRegion region;

                if (isInGrid) {
                    Slot slot = grid[c][k];
                    slot.updatePulse(selected, delta);
                    slot.tickScale(delta);

                    // wobble on HOVER entry even if selected
                    slot.updateHoverWobble(hovered, delta);
                    symbolsScale = slot.scale * slot.pulseScale() * slot.wobbleScale();
                    if (runningResults && !slot.isInPatternHit()) alpha = 0.5f;
                }

                float drawW = CELL_W * symbolsScale;
                float drawH = CELL_H * symbolsScale;
                float adjX = drawX - (drawW - CELL_W) / 2f;
                float adjY = drawY - (drawH - CELL_H) / 2f;

                // choose frame (keeps your animated border when selected)
                region = Assets.I().getSymbol(symbolSlot.getSymbol());

                // NEW: rotate around center using current wobble angle
                float rotation = isInGrid ? grid[c][k].wobbleAngleDeg() : 0f;

                batch.setColor(Assets.I().shadowColor());
                batch.draw(
                    Assets.I().getSymbol(symbolSlot.getSymbol()),
                    adjX + 0.05f, adjY - 0.05f,
                    drawW / 2f, drawH / 2f,
                    drawW, drawH,
                    1f, 1f,
                    rotation
                );
                batch.setColor(1f, 1f, 1f, alpha);

                // Draw with origin at the center, width/height already scaled
                batch.draw(
                    region,
                    adjX, adjY,
                    drawW / 2f, drawH / 2f,   // originX, originY
                    drawW, drawH,
                    1f, 1f,                   // scale already baked into drawW/H
                    rotation
                );
                batch.setColor(1f, 1f, 1f, 1f);

                if (symbolSlot.getStatUpgrade() != null) {
                    batch.draw(
                        new TextureRegion(symbolSlot.getStatUpgrade().getStat().getTexture()),
                        adjX + 1.45f, adjY + 0.6f,
                        (drawW - 2f) / 2f, (drawW - 2f) / 2f,
                        (drawW - 1.8f), (drawW - 1.8f),
                        1f, 1f,
                        rotation + 180
                    );
                }
            }
        }
    }

    // --- spin control (organic staggered start/stop, aligned to center row) ---
    public void spin() {
        // tuning knobs
        float startSpeed = 16f;      // symbols/sec target cruise
        float startStagger = 0.15f;  // delay between reel starts
        float stopStagger = 0.35f;  // delay between reel stops (after starts)

        // start + schedule stop per reel
        for (int c = 0; c < cols; c++) {
            final int col = c;
            float startDelay = c * startStagger;
            float stopDelay = cols * startStagger + c * stopStagger;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    reels.get(col).start(startSpeed);
                }
            }, startDelay);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    reels.get(col).stopSoonAlignCenter();
                }
            }, stopDelay);
        }
    }

    // Returns each matching line as a List<Slot>
    public List<SlotMatch> findMatches() {
        SymbolInstance[][] slotMap = new SymbolInstance[cols][rows];

        for (int c = 0; c < reels.size(); c++) {
            for (int row = 0; row < rows; row++) {
                slotMap[c][row] = reels.get(c).slotAtRow(row);
            }
        }

        // Raw matches (symbol + positions)
        List<PatternMatch> matches = PatternFinder.findMatches(slotMap);

        // Build final slot-based matches
        List<SlotMatch> result = new ArrayList<>();

        for (PatternMatch match : matches) {

            List<Slot> slots = new ArrayList<>();
            for (Vector2 pos : match.getPositions()) {
                Slot slot = grid[(int) pos.x][(int) pos.y];
//                slot.setInPatternHit(true);
                slots.add(slot);
            }
            result.add(new SlotMatch(match.getSymbol(), slots));
        }

        Collections.sort(result, new Comparator<SlotMatch>() {
            @Override
            public int compare(SlotMatch o1, SlotMatch o2) {
                return o1.getSymbol().ordinal() - o2.getSymbol().ordinal();
            }
        });
        return result;
    }

    public static Rectangle getBounds() {
        return new Rectangle(
            originX,
            originY,
            cols * (CELL_W + spacingX),
            rows * (CELL_H + spacingY)
        );
    }

    public void setAlpha(float value) {
        desiredAlpha = value;
    }

    public List<Reel> getReels() {
        return reels;
    }

    public boolean isRunningResults() {
        return runningResults;
    }

    public void setRunningResults(boolean runningResults) {
        this.runningResults = runningResults;
    }
}
