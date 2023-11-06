package me.choicore.common.distributedlock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Getter
@Setter(AccessLevel.PROTECTED)
public class DefaultDistributedLockDefinition implements DistributedLockDefinition, Serializable {

    public static final long DEFAULT_WAIT_TIME = 5;
    public static final long DEFAULT_LEASE_TIME = 3;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    private String key;
    private long waitTime = DEFAULT_WAIT_TIME;
    private long leaseTime = DEFAULT_LEASE_TIME;
    private TimeUnit timeUnit = DEFAULT_TIME_UNIT;

    public DefaultDistributedLockDefinition() {
    }

    public DefaultDistributedLockDefinition(
            final long waitTime,
            final long leaseTime,
            final TimeUnit timeUnit
    ) {
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
        this.timeUnit = timeUnit;
    }

    public DefaultDistributedLockDefinition(
            DistributedLockDefinition distributedLockDefinition
    ) {
        this.waitTime = distributedLockDefinition.getWaitTime();
        this.leaseTime = distributedLockDefinition.getLeaseTime();
        this.timeUnit = distributedLockDefinition.getTimeUnit();
    }
}