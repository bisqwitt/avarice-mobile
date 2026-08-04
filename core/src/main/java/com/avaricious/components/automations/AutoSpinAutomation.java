package com.avaricious.components.automations;

import com.avaricious.components.ButtonBoard;
import com.avaricious.components.roundInfoPanel.AutoSpinDisplay;

public class AutoSpinAutomation extends AbstractAutomation {

    @Override
    protected void onActivate() {
        ButtonBoard.I().activateAutoSpin();
        AutoSpinDisplay.I().show();
    }

}
