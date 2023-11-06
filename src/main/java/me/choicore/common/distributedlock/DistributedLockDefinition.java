package me.choicore.common.distributedlock;

import java.util.concurrent.TimeUnit;

public interface DistributedLockDefinition {
    String getKey();

    long getWaitTime();

    long getLeaseTime();

    TimeUnit getTimeUnit();
}
