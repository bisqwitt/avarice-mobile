package com.avaricious.stats.statupgrades;

import com.avaricious.stats.PlayerStats;
import com.avaricious.upgrades.ApplieableRelic;
import com.avaricious.upgrades.Relic;

import java.math.BigDecimal;

public class StatusRelic extends Relic implements ApplieableRelic {

    public static StatusRelic newRandom() {
//        if (MathUtils.random(1, 20) != 1) {
//            return null;
//        }
        return new StatusRelic(PlayerStats.I().getRandomStat(), new BigDecimal("0.05"));
    }

    private final Stat stat;
    private final BigDecimal additionalPercentage;

    private StatusRelic(Stat stat, BigDecimal percentage) {
        this.stat = stat;
        this.additionalPercentage = percentage;
    }

    public Stat getStat() {
        return stat;
    }

    @Override
    public String description() {
        return "Increase " + stat.getClass().getSimpleName() + " by " + (additionalPercentage.floatValue() * 100) + "%";
    }

    @Override
    public void apply() {
        stat.setPercentage(stat.getPercentage().add(additionalPercentage));
    }

    public int getAdditionalPercentage() {
        return (int) (additionalPercentage.floatValue() * 100);
    }
}
