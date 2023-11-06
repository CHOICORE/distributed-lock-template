package me.choicore.common.distributedlock;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "distributed-lock")
public record DistributedLockProperties(
        long leaseTime,
        long waitTime,
        TimeUnit timeUnit
) {
}
