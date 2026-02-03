package com.avaricious.stats;

import com.avaricious.stats.statupgrades.CreditSpawnChance;
import com.avaricious.stats.statupgrades.CriticalHitChance;
import com.avaricious.stats.statupgrades.DoubleHitChance;
import com.avaricious.stats.statupgrades.EvadeChance;
import com.avaricious.stats.statupgrades.LuckChance;
import com.avaricious.stats.statupgrades.Stat;
import com.badlogic.gdx.math.MathUtils;

import java.util.List;
import java.util.Map;

public class PlayerStats {

    private static PlayerStats instance;

    public static PlayerStats I() {
        return instance == null ? instance = new PlayerStats() : instance;
    }

    private final Map<Class<? extends Stat>, Stat> stats = Map.of(
        CreditSpawnChance.class, new CreditSpawnChance(),
        CriticalHitChance.class, new CriticalHitChance(),
        DoubleHitChance.class, new DoubleHitChance(),
        EvadeChance.class, new EvadeChance(),
        LuckChance.class, new LuckChance()
    );

    private PlayerStats() {
    }

    public boolean rollChance(Class<? extends Stat> statClass) {
        return stats.get(statClass).rollChance();
    }

    public <T extends Stat> T getStat(Class<T> statClass) {
        return statClass.cast(stats.get(statClass));
    }

    public Stat getRandomStat() {
        List<Stat> values = stats.values().stream().toList();
        return values.get(MathUtils.random(0, values.size() - 1));
    }

}
