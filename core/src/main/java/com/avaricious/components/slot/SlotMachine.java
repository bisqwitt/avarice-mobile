package com.avaricious.components.slot;

import com.avaricious.DevTools;
import com.avaricious.Main;
import com.avaricious.RoundsManager;
import com.avaricious.bosses.LemonDebuffBoss;
import com.avaricious.components.slot.pattern.PatternFinder;
import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.components.slot.pattern.PatternMatch;
import com.avaricious.effects.TextureEcho;
import com.avaricious.utility.AssetKey;
import com.avaricious.utility.Assets;
import com.avaricious.utility.Pencil;
import com.avaricious.utility.TextureDrawing;
import com.avaricious.utility.ZIndex;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SlotMachine {

    private static SlotMachine instance;

    public static SlotMachine I() {
        return instance == null ? instance = new SlotMachine() : instance;
    }

    // --- Layout ---
    public static final int cols = 5;
    public static final int rows = 3;
    public static final float CELL_W = 1.7f;
    public static final float CELL_H = 1.7f;
    public static final float spacingX = 0f;
    public static final float spacingY = 0.15f;

    public static final float originX = 0.2f;
    public static final float originY = 8.9f;

    public static final Rectangle windowBounds = new Rectangle(0.05f, 8.8f, 8.85f, 5.6f);

    // Visual cells (for selection pulse/scale)
    private final Body[][] grid = new Body[cols][rows];
    private final TextureRegion slotBox = Assets.I().get(AssetKey.SLOT_BOX);

    // Reels (one per column)
    private final List<Reel> reels = new ArrayList<>();

    private boolean runningResults = false;
    private float alpha = 1f;
    private float desiredAlpha = 1f;

    private Runnable onLastReelFinished;
    private int spinningReels = 0;

    private boolean stale = true;

    private SlotMachine() {
        // build visual cells
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (r == 0) grid[c][r] = new Body(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 2 * (CELL_H + spacingY)
                ));
                if (r == 1) grid[c][r] = new Body(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 1 * (CELL_H + spacingY)
                ));
                if (r == 2) grid[c][r] = new Body(new Vector2(
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
            reels.add(new Reel(baseStrip, rows, () -> {
                spinningReels--;
                if (spinningReels == 0) onLastReelFinished.run();
            }));
        }
    }

    // --- drawing ---
    public void draw(Main app, float delta) {
        SpriteBatch batch = app.getBatch();
        // update reel motion
        for (int c = 0; c < cols; c++) {
            reels.get(c).update(delta);
        }
        Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .filter(Objects::nonNull)
            .forEach(body -> {
                body.update(delta);
            });

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
        area.setY(area.y - 0.125f);
        area.setHeight(area.height);

        Pencil.I().startScissors(cam, batch.getTransformMatrix(), area);
        drawSymbols(delta);
        Pencil.I().endScissors();

        TextureEcho.draw(batch, delta, TextureEcho.Type.SLOT);
    }

    private void drawSymbols(float delta) {
        for (int c = 0; c < cols; c++) {
            int drawFrom = -1;
            int drawTo = rows - 1;

            for (int k = drawFrom; k <= drawTo; k++) {
                Vector2 pos = new Vector2(c, k);
                drawSymbol(pos, delta);
            }
        }
    }

    private void drawSymbol(Vector2 gridPos, float delta) {
        Reel reel = reels.get((int) gridPos.x);
        Symbol symbol = reel.symbolAtRow((int) gridPos.y);

        final float stepX = CELL_W + spacingX;
        float colX = originX + gridPos.x * stepX;

        boolean isInGrid = isInGrid(gridPos);

        final float stepY = (CELL_H + spacingY);
        final float topY = originY + (rows - 1) * stepY;
        float drawY = topY - (gridPos.y + reel.frac()) * stepY;

        boolean selected = false;
        boolean hovered = false;
        float symbolsScale = 1f;
        float alpha = this.alpha;

        TextureRegion region;

        if (isInGrid) {
            Body body = grid[(int) gridPos.x][(int) gridPos.y];
            symbolsScale = body.scale;
            if (runningResults && !body.isInPatternHit()) alpha = 0.5f;
        }
        if (RoundsManager.I().getBoss() instanceof LemonDebuffBoss && symbol == Symbol.LEMON)
            alpha = 0.5f;

        float drawW = CELL_W;
        float drawH = CELL_H;
        float adjX = colX - (drawW - CELL_W) / 2f;
        float adjY = drawY - (drawH - CELL_H) / 2f;

        // choose frame (keeps your animated border when selected)
        region = Assets.I().getSymbol(symbol);

        // NEW: rotate around center using current wobble angle
//        float rotation = isInGrid ? grid[(int) gridPos.x][(int) gridPos.y].wobbleAngleDeg() : 0f;
        float scale = isInGrid ? grid[(int) gridPos.x][(int) gridPos.y].getScale() : 1f;
        float rotation = isInGrid ? grid[(int) gridPos.x][(int) gridPos.y].getRotation() : 0f;

        Pencil.I().addDrawing(new TextureDrawing(
            region,
            new Rectangle(adjX, adjY, drawW, drawH),
            scale, rotation, ZIndex.SLOT_MACHINE, new Color(1f, 1f, 1f, alpha)
        ));
    }

    // --- spin control (organic staggered start/stop, aligned to center row) ---
    public void spin() {
        spinningReels = 5;
        stale = false;

        float startSpeed = 16f;
        float startStagger = 0.1f;
        float stopStagger = 0.5f;

        for (int c = 0; c < cols; c++) {
            final int col = c;
            float startDelay = c * startStagger;
            float stopDelay = cols * startStagger + c * stopStagger;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    reels.get(col).start(startSpeed + MathUtils.random(-0.7f, 0.7f)); // tiny per-reel variation
                }
            }, startDelay);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    reels.get(col).requestStopAlignCenter(0); // at least 2 full strip loops after stop requested
                }
            }, 1f + stopDelay);
        }
    }

    // Returns each matching line as a List<Slot>
    public List<PatternHitContext> findMatches() {
        Symbol[][] symbolMap = new Symbol[cols][rows];

        for (int c = 0; c < reels.size(); c++) {
            for (int row = 0; row < rows; row++) {
                symbolMap[c][row] = reels.get(c).symbolAtRow(row);
            }
        }

        // Raw matches (symbol + positions)
        List<PatternMatch> matches = PatternFinder.findMatches(symbolMap);

        // Build final slot-based matches
        List<PatternHitContext> result = new ArrayList<>();

        for (PatternMatch match : matches) {

            List<Body> bodies = new ArrayList<>();
            for (Vector2 pos : match.getPositions()) {
                Body body = grid[(int) pos.x][(int) pos.y];
//                slot.setInPatternHit(true);
                bodies.add(body);
            }
            result.add(new PatternHitContext(match.getSymbol(), bodies));
        }

        result.sort(new Comparator<PatternHitContext>() {
            @Override
            public int compare(PatternHitContext o1, PatternHitContext o2) {
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

    private boolean isInGrid(Vector2 pos) {
        return pos.y >= 0 && pos.y < rows;
    }

    public void setAlpha(float value) {
        desiredAlpha = value;
    }

    public void setRunningResults(boolean runningResults) {
        this.runningResults = runningResults;
    }

    public void setStale(boolean stale) {
        this.stale = stale;
    }

    public Symbol[][] getSymbolMap() {
        Symbol[][] symbolMap = new Symbol[cols][rows];
        for (int c = 0; c < reels.size(); c++) {
            for (int row = 0; row < rows; row++) {
                symbolMap[c][row] = reels.get(c).symbolAtRow(row);
            }
        }
        return symbolMap;
    }

    public void setOnLastReelFinished(Runnable onLastReelFinished) {
        this.onLastReelFinished = onLastReelFinished;
    }

    public boolean isStale() {
        return stale;
    }
}
