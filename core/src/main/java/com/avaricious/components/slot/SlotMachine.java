package com.avaricious.components.slot;

import com.avaricious.Main;
import com.avaricious.RoundsManager;
import com.avaricious.bosses.CherryDebuffBoss;
import com.avaricious.bosses.LemonDebuffBoss;
import com.avaricious.components.RingBar;
import com.avaricious.components.slot.pattern.PatternFinder;
import com.avaricious.components.slot.pattern.PatternHitContext;
import com.avaricious.components.slot.pattern.PatternMatch;
import com.avaricious.effects.TextureEcho;
import com.avaricious.items.upgrades.rings.DoubleSymbolValueDisableFruits;
import com.avaricious.utility.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public static final int rows = 5;
    public static final float CELL_W = 1.4f;
    public static final float CELL_H = 1.4f;
    public static final float spacingX = 0.35f;
    public static final float spacingY = 0.15f;

    public static final float originX = 0.25f;
    public static final float originY = 6.5f;

    public static final Rectangle windowBounds = new Rectangle(originX - 0.23f, originY - 0.325f, 8.95f, 6.425f);

    private final List<Reel> reels = new ArrayList<>();
    private final Body[][] grid = new Body[cols][rows];
    private final GlyphLayout bossDescription = new GlyphLayout();

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
                    originY + 4 * (CELL_H + spacingY)
                ));
                if (r == 1) grid[c][r] = new Body(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 3 * (CELL_H + spacingY)
                ));
                if (r == 2) grid[c][r] = new Body(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 2 * (CELL_H + spacingY)
                ));
                if (r == 3) grid[c][r] = new Body(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 1 * (CELL_H + spacingY)
                ));
                if (r == 4) grid[c][r] = new Body(new Vector2(
                    originX + c * (CELL_W + spacingX),
                    originY + 0 * (CELL_H + spacingY)
                ));
            }
        }

        for (int c = 0; c < cols; c++) {
            reels.add(new Reel(rows, () -> {
                spinningReels--;
                if (spinningReels == 0) onLastReelFinished.run();
            }));
        }
        buildStrip();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if(grid[i][j] != null) {
                    grid[i][j].idleSwayEffect.setStrength(2.5f, 0.8f);
                }
            }
        }
    }

    private void update(float delta) {
        for (int c = 0; c < cols; c++) {
            reels.get(c).update(delta);
        }

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if(grid[i][j] != null) {
                    grid[i][j].update(delta);
                }
            }
        }

        if (desiredAlpha != alpha) {
            float speed = 10f; // higher = faster convergence
            alpha = MathUtils.lerp(alpha, desiredAlpha, speed * delta);
        }
    }

    public void draw(Main app, float delta) {
        SpriteBatch batch = app.getBatch();
        // update reel motion
        update(delta);

//        Pencil.I().addDrawing(new TextureDrawing(Assets.I().get(AssetKey.WHITE_PIXEL),
//            0, originY + 6.13f, 9, 0.05f, ZIndex.SLOT_MACHINE));
//        Pencil.I().addDrawing(new TextureDrawing(Assets.I().get(AssetKey.WHITE_PIXEL),
//            0, originY - 0.4f, 9, 0.05f, ZIndex.SLOT_MACHINE));

//        if (RoundsManager.I().isBossRound()) {
//            bossDescription.setText(Assets.I().getSmallFont(), "Boss Round! " + RoundsManager.I().getBoss().description());
//            Pencil.I().addDrawing(new FontDrawing(Assets.I().getSmallFont(), bossDescription,
//                new Vector2(0.5f * 100, 14.5f * 100), ZIndex.SLOT_MACHINE));
//        }

        Rectangle area = getBounds(); // world-space
        area.setX(area.x - 0.3f);
        area.setY(area.y - 0.35f);
        area.setWidth(area.width + 0.3f);
        area.setHeight(area.height + 0.25f);

        Camera cam = app.getViewport().getCamera();
        cam.update();
        Pencil.I().startScissors(cam, batch.getTransformMatrix(), area);
        List<Vector2> symbolsInPatternHit = drawSymbols();
        Pencil.I().endScissors();

        Seq.of(symbolsInPatternHit).forEach(this::drawSymbol);

        TextureEcho.draw(batch, delta, TextureEcho.Type.SLOT);
    }

    private List<Vector2> drawSymbols() {
        List<Vector2> symbolsInPatternHit = new ArrayList<>();
        for (int c = 0; c < cols; c++) {
            int drawFrom = -1;
            int drawTo = rows - 1;

            for (int k = drawFrom; k <= drawTo; k++) {
                Vector2 pos = new Vector2(c, k);
                if (isInGrid(pos) && grid[c][k].isInPatternHit()) {
                    symbolsInPatternHit.add(pos);
                    continue;
                }
                drawSymbol(pos);
            }
        }
        return symbolsInPatternHit;
    }

    private void drawSymbol(Vector2 gridPos) {
        Reel reel = reels.get((int) gridPos.x);
        Symbol symbol = reel.symbolAtRow((int) gridPos.y);

        final float stepX = CELL_W + spacingX;
        float colX = originX + gridPos.x * stepX;

        boolean isInGrid = isInGrid(gridPos);

        final float stepY = (CELL_H + spacingY);
        final float topY = originY + (rows - 1) * stepY;
        float drawY = topY - (gridPos.y + reel.frac()) * stepY;

        float drawW = CELL_W;
        float drawH = CELL_H;
        float adjX = colX - (drawW - CELL_W) / 2f;
        float adjY = drawY - 0.08f - (drawH - CELL_H) / 2f;
        float alpha = this.alpha;

        if (isInGrid) {
            Body body = grid[(int) gridPos.x][(int) gridPos.y];
            adjY += body.getIdleFloatYOffset();
            if (runningResults && !body.isInPatternHit()) alpha = 0.5f;
        }

//        if (adjY > 13.55f) {
//            float t = 1f - ((adjY - 13.55f) / 0.5f);
//            alpha *= Math.max(0f, Math.min(1f, t));
//        } else if (adjY < 8.45f) {
//            float t = 1f - ((8.45f - adjY) / 0.5f);
//            alpha *= Math.max(0f, Math.min(1f, t));
//        }

        if (RoundsManager.I().getBoss() instanceof LemonDebuffBoss && symbol == Symbol.LEMON
            || RoundsManager.I().getBoss() instanceof CherryDebuffBoss && symbol == Symbol.CHERRY
            || RingBar.I().ringOwned(DoubleSymbolValueDisableFruits.class) && symbol.isFruit())
            alpha = 0.5f;

        float scale = isInGrid ? grid[(int) gridPos.x][(int) gridPos.y].getScale() : 1f;
        float rotation = isInGrid ? grid[(int) gridPos.x][(int) gridPos.y].getRotation() : 0f;

        Color shadowColor = Assets.I().shadowColor();
        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().get(symbol.shadowKey()),
            adjX, adjY - 0.1f, drawW, drawH,
            scale, rotation, ZIndex.SLOT_MACHINE, new Color(shadowColor.r, shadowColor.g, shadowColor.b, Math.min(shadowColor.a, alpha))
        ));

        Pencil.I().addDrawing(new TextureDrawing(
            Assets.I().getSymbol(symbol),
            adjX, adjY, drawW, drawH,
            scale, rotation, ZIndex.SLOT_MACHINE, new Color(1f, 1f, 1f, alpha)
        ));
    }

    // --- spin control (organic staggered start/stop, aligned to center row) ---
//    public void spin() {
//        spinningReels = cols;
//        stale = false;
//
//        float startSpeed = 16f;
//        float startStagger = 0.1f;
//        float stopStagger = 0.25f;
//
//        for (int c = 0; c < cols; c++) {
//            final int col = c;
//
//            float startDelay = col * startStagger;
//            float stopDelay = 0.5f + col * stopStagger;
//
//            float reelSpeed = startSpeed + SeededRandomizer.nextFloat(-0.7f, 0.7f);
//
//            Reel reel = reels.get(col);
//            int targetIndex = SeededRandomizer.nextInt(0, reel.stripSize() - 1);
//
//            Timer.schedule(new Timer.Task() {
//                @Override
//                public void run() {
//                    reels.get(col).start(reelSpeed);
//                }
//            }, startDelay);
//
//            Timer.schedule(new Timer.Task() {
//                @Override
//                public void run() {
//                    int minDistance = 8 + col * 4;
//                    reels.get(col).requestStopAtIndex(targetIndex, minDistance);
//                }
//            }, 0.6f + col * 0.12f);
//        }
//    }

    public void spin() {
        spinningReels = cols;
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

        result.sort(Comparator.comparingInt(o -> o.getSymbol().ordinal()));
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

    public void buildStrip() {
        List<Symbol> baseStrip = new ArrayList<>();

        Seq.of(Arrays.asList(Symbol.values()))
            .forEach(symbol -> {
                for (int i = 0; i < symbol.poolCount() / 2; i++) {
                    baseStrip.add(symbol);
                }
            });

        for (Reel reel : reels) {
            List<Symbol> reelStrip = new ArrayList<>(baseStrip);
            Collections.shuffle(reelStrip, SeededRandomizer.get());
            reel.setStrip(reelStrip);
        }

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
