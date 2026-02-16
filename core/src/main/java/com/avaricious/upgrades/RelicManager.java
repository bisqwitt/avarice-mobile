package com.avaricious.upgrades;

import java.util.ArrayList;
import java.util.List;

public class RelicManager {

    private static RelicManager instance;

    public static RelicManager I() {
        return instance == null ? instance = new RelicManager() : instance;
    }

    private final List<Relic> relics = new ArrayList<>();

    public <T> List<T> getRelicsOfClass(Class<T> clazz) {
        List<T> out = new ArrayList<>();
        for (int i = 0; i < relics.size(); i++) {          // deck is a List<?>
            Object o = relics.get(i);
            if (clazz.isInstance(o)) {
                out.add(clazz.cast(o));
            }
        }
        return out;
    }

    public <T> T getRelicOfClass(Class<T> upgradeClass) {
        for (Relic upgrade : relics) {
            if (upgradeClass.isInstance(upgrade)) return (T) upgrade;
        }
        return null;
    }

    public boolean relicOwned(Class<? extends Relic> relicClass) {
        for (Relic relic : relics) {
            if (relicClass.isInstance(relic)) return true;
        }
        return false;
    }

}
