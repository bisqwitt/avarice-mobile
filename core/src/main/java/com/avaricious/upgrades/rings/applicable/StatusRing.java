package com.avaricious.upgrades.rings.applicable;

import com.avaricious.stats.PlayerStats;
import com.avaricious.stats.statupgrades.Stat;
import com.avaricious.upgrades.rings.AbstractRing;
import com.avaricious.utility.RingAssetKeys;

import java.math.BigDecimal;

public class StatusRing extends AbstractRing implements IApplicableRing {

    public static StatusRing newRandom() {
//        if (MathUtils.random(1, 20) != 1) {
//            return null;
//        }
        return new StatusRing(PlayerStats.I().getRandomStat(), new BigDecimal("0.05"));
    }

    private final Stat stat;
    private final BigDecimal additionalPercentage;

    private StatusRing(Stat stat, BigDecimal percentage) {
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

    @Override
    public RingAssetKeys keySet() {
        return RingAssetKeys.RING_58;
    }
}
