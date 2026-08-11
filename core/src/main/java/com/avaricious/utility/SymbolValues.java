package com.avaricious.utility;

import com.avaricious.components.slot.Symbol;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SymbolValues {

    private static SymbolValues instance;

    public static SymbolValues I() {
        return instance == null ? instance = new SymbolValues() : instance;
    }

    private int lemonValue = 2;
    private int cherryValue = 2;
    private int cloverValue = 3;
    private int bellValue = 3;
    private int ironValue = 5;
    private int diamondValue = 5;
    private int sevenValue = 7;

    private final PropertyChangeSupport symbolValueChangeSupport = new PropertyChangeSupport(this);

    private SymbolValues() {
    }

    public int getValue(Symbol symbol) {
        switch (symbol) {
            case LEMON:
                return lemonValue;
            case CHERRY:
                return cherryValue;
            case CLOVER:
                return cloverValue;
            case BELL:
                return bellValue;
            case IRON:
                return ironValue;
            case DIAMOND:
                return diamondValue;
        }
        return sevenValue;
    }

    public void increaseValue(Symbol symbol) {
        switch (symbol) {
            case LEMON:
                lemonValue++;
                symbolValueChangeSupport.firePropertyChange(Symbol.LEMON.toString(), lemonValue - 1, lemonValue);
                break;

            case CHERRY:
                cherryValue++;
                symbolValueChangeSupport.firePropertyChange(Symbol.CHERRY.toString(), cherryValue - 1, cherryValue);
                break;

            case CLOVER:
                cloverValue++;
                symbolValueChangeSupport.firePropertyChange(Symbol.CLOVER.toString(), cloverValue - 1, cloverValue);
                break;

            case BELL:
                bellValue++;
                symbolValueChangeSupport.firePropertyChange(Symbol.BELL.toString(), bellValue - 1, bellValue);
                break;

            case IRON:
                ironValue++;
                symbolValueChangeSupport.firePropertyChange(Symbol.IRON.toString(), ironValue - 1, ironValue);
                break;

            case DIAMOND:
                diamondValue++;
                symbolValueChangeSupport.firePropertyChange(Symbol.DIAMOND.toString(), diamondValue - 1, diamondValue);
                break;

            case SEVEN:
                sevenValue++;
                symbolValueChangeSupport.firePropertyChange(Symbol.SEVEN.toString(), sevenValue - 1, sevenValue);
                break;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        symbolValueChangeSupport.addPropertyChangeListener(listener);
    }

}
