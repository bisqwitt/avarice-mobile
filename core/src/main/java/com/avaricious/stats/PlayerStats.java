package com.avaricious.stats;

import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CriticalHitChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.stats.statupgrades.EvadeChance;
import com.avaricious.stats.statupgrades.LuckChance;
import com.avaricious.stats.statupgrades.Stat;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerStats {

    private static PlayerStats instance;

    public static PlayerStats I() {
        return instance == null ? instance = new PlayerStats() : instance;
    }

    private final Map<Class<? extends Stat>, Stat> stats = new HashMap<>();

    private PlayerStats() {
        stats.put(CreditSpawnChance.class, new CreditSpawnChance());
        stats.put(CriticalHitChance.class, new CriticalHitChance());
        stats.put(DoubleHitChance.class, new DoubleHitChance());
        stats.put(EvadeChance.class, new EvadeChance());
        stats.put(LuckChance.class, new LuckChance());
    }

    public boolean rollChance(Class<? extends Stat> statClass) {
        return stats.get(statClass).rollChance();
    }

    public <T extends Stat> T getStat(Class<T> statClass) {
        return statClass.cast(stats.get(statClass));
    }

    public Stat getRandomStat() {
        List<Stat> values = new ArrayList<>(stats.values());
        return values.get(MathUtils.random(0, values.size() - 1));
    }

}
