package com.avaricious.upgrades;

import com.avaricious.Assets;

public class DeptRelic extends Relic {

    @Override
    public String description() {
        return "Go up to " + Assets.I().yellowText("-20$") + " in dept";
    }
}
