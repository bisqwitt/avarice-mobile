package com.avaricious.upgrades;

import java.util.ArrayList;
import java.util.List;

public class RelicManager {

    private static RelicManager instance;

    public static RelicManager I() {
        return instance == null ? instance = new RelicManager() : instance;
    }

    private final List<Ring> rings = new ArrayList<>();

    public <T> List<T> getRelicsOfClass(Class<T> clazz) {
        List<T> out = new ArrayList<>();
        for (int i = 0; i < rings.size(); i++) {          // deck is a List<?>
            Object o = rings.get(i);
            if (clazz.isInstance(o)) {
                out.add(clazz.cast(o));
            }
        }
        return out;
    }

    public <T> T getRelicOfClass(Class<T> upgradeClass) {
        for (Ring upgrade : rings) {
            if (upgradeClass.isInstance(upgrade)) return (T) upgrade;
        }
        return null;
    }

    public boolean relicOwned(Class<? extends Ring> relicClass) {
        for (Ring ring : rings) {
            if (relicClass.isInstance(ring)) return true;
        }
        return false;
    }

}
