package com.avaricious.components.slot;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class BouncingSymbolManager {

    private static BouncingSymbolManager instance;

    public static BouncingSymbolManager I() {
        return instance == null ? instance = new BouncingSymbolManager() : instance;
    }

    private final List<BouncingSymbol> bouncingSymbols = new ArrayList<>();

    private BouncingSymbolManager() {
    }

    public void createFallingSymbol(Symbol symobl, float x, float y) {
        bouncingSymbols.add(new BouncingSymbol(symobl, x, y));
    }

    public void handleInput(Vector2 mouse, boolean touching, boolean wasTouching) {
        for (BouncingSymbol symbol : bouncingSymbols) {
            symbol.handleInput(mouse, touching, wasTouching);
        }
    }

    public void drawFallingSymbols(float delta) {
        updateFallingSymbols(delta);
        for (BouncingSymbol symbol : bouncingSymbols) {
            symbol.draw();
        }
    }

    private void updateFallingSymbols(float delta) {
        for (BouncingSymbol symbol : bouncingSymbols) {
            symbol.update(delta);
        }

        handleSymbolCollisions();
        bouncingSymbols.removeIf(BouncingSymbol::isFinished);
    }

    private void handleSymbolCollisions() {
        for (int i = 0; i < bouncingSymbols.size(); i++) {
            BouncingSymbol a = bouncingSymbols.get(i);

            for (int j = i + 1; j < bouncingSymbols.size(); j++) {
                BouncingSymbol b = bouncingSymbols.get(j);
                resolveCollision(a, b);
            }
        }
    }

    private void resolveCollision(
        BouncingSymbol a,
        BouncingSymbol b
    ) {

        float dx = b.getCenterX() - a.getCenterX();
        float dy = b.getCenterY() - a.getCenterY();

        float distanceSquared = dx * dx + dy * dy;

        float minDistance =
            a.getRadius() + b.getRadius();

        if (distanceSquared >= minDistance * minDistance) {
            return;
        }

        float distance = (float) Math.sqrt(distanceSquared);

        /*
         * Exact same position.
         * Prevent division by zero.
         */
        if (distance < 0.001f) {
            dx = MathUtils.random(-1f, 1f);
            dy = MathUtils.random(-1f, 1f);

            distance =
                (float) Math.sqrt(
                    dx * dx + dy * dy
                );
        }

        /*
         * Collision normal.
         */
        float normalX = dx / distance;
        float normalY = dy / distance;

        /*
         * Separate overlapping symbols.
         */
        float overlap =
            minDistance - distance;

        float separation =
            overlap * 0.5f;

        a.move(
            -normalX * separation,
            -normalY * separation
        );

        b.move(
            normalX * separation,
            normalY * separation
        );

        /*
         * Relative velocity.
         */
        float relativeVelocityX =
            b.getVelocityX() - a.getVelocityX();

        float relativeVelocityY =
            b.getVelocityY() - a.getVelocityY();

        /*
         * Velocity along collision normal.
         */
        float velocityAlongNormal =
            relativeVelocityX * normalX
                + relativeVelocityY * normalY;

        /*
         * They're already separating.
         */
        if (velocityAlongNormal > 0f) {
            return;
        }

        /*
         * Slightly randomized elasticity makes collisions
         * feel less mechanically perfect.
         */
        float restitution =
            MathUtils.random(0.55f, 0.8f);

        /*
         * Equal mass collision.
         */
        float impulse =
            -(1f + restitution)
                * velocityAlongNormal
                / 2f;

        float impulseX =
            impulse * normalX;

        float impulseY =
            impulse * normalY;

        a.setVelocityX(
            a.getVelocityX() - impulseX
        );

        a.setVelocityY(
            a.getVelocityY() - impulseY
        );

        b.setVelocityX(
            b.getVelocityX() + impulseX
        );

        b.setVelocityY(
            b.getVelocityY() + impulseY
        );

        /*
         * Tangential interaction creates tumbling.
         */
        float tangentX = -normalY;
        float tangentY = normalX;

        float tangentVelocity =
            relativeVelocityX * tangentX
                + relativeVelocityY * tangentY;

        float spin =
            tangentVelocity
                * MathUtils.random(8f, 16f);

        a.addRotationVelocity(-spin);
        b.addRotationVelocity(spin);

        /*
         * Tiny random spin variation prevents
         * identical-looking collisions.
         */
        a.addRotationVelocity(
            MathUtils.random(-25f, 25f)
        );

        b.addRotationVelocity(
            MathUtils.random(-25f, 25f)
        );
    }

}
