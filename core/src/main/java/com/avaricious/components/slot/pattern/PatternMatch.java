// com.avaricious.components.slot.pattern.PatternMatch

package com.avaricious.components.slot.pattern;

import com.avaricious.components.slot.Symbol;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class PatternMatch {

    private final Symbol symbol;
    private final int length;
    private final List<Vector2> positions;
    private final PatternDirection direction;

    public PatternMatch(Symbol symbol, int length, List<Vector2> positions, PatternDirection direction) {
        this.symbol = symbol;
        this.length = length;
        this.positions = positions;
        this.direction = direction;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public int getLength() {
        return length;
    }

    public List<Vector2> getPositions() {
        return positions;
    }

    public PatternDirection getDirection() {
        return direction;
    }
}
