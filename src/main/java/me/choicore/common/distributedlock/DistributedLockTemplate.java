package me.choicore.common.distributedlock;

import me.choicore.common.distributedlock.exception.DistributedLockException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class DistributedLockTemplate extends DefaultDistributedLockDefinition implements DistributedLockOperations {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockTemplate.class);

    private final RedissonClient redissonClient;

    private final TransactionTemplate transactionTemplate;
    private boolean withInTransactional = true;

    public DistributedLockTemplate(
            final RedissonClient redissonClient,
            final DistributedLockDefinition distributedLockDefinition,
            final TransactionTemplate transactionTemplate
    ) {
        super(distributedLockDefinition);
        this.redissonClient = redissonClient;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> T execute(Supplier<T> supplier) {
        final RLock rLock = this.getDistributedLock();
        try {
            this.validateTryAcquireLock(rLock);

            log.info("[Thread:{}] <{}> lock acquired with in transaction : {}", Thread.currentThread().threadId(), rLock.getName(), withInTransactional);

            return executeSupplier(supplier);

        } catch (InterruptedException e) {
            throw new DistributedLockException("Lock acquisition was interrupted", e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            release(rLock);
        }
    }

    @Override
    public <T> T execute(String prefix, Set<Long> keys, Supplier<T> supplier) {
        final RLock[] rLocks = keys.stream().sorted().map(key -> this.redissonClient.getLock(prefix + "-" + key)).toArray(RLock[]::new);

        try {
            boolean available = this.redissonClient.getMultiLock(rLocks).tryLock(super.getWaitTime(), super.getLeaseTime(), super.getTimeUnit());

            if (!available) {
                throw new DistributedLockException("Failed to acquire lock");
            }

            String lockNames = Arrays.stream(rLocks).map(RLock::getName).collect(Collectors.joining(", "));
            return executeSupplier(supplier);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            for (RLock rLock : rLocks) {
                release(rLock);
            }
        }
    }

    private <T> T executeWithinTransaction(final Supplier<T> supplier) {
        return transactionTemplate.execute(status -> supplier.get());
    }

    private <T> T executeSupplier(Supplier<T> supplier) {
        if (this.withInTransactional) {
            return this.executeWithinTransaction(supplier);
        } else {
            return supplier.get();
        }
    }

    public DistributedLockTemplate withInTransaction(boolean withTransaction) {
        this.withInTransactional = withTransaction;
        return this;
    }

    public DistributedLockTemplate key(final String key) {
        this.setKey(key);
        return this;
    }

    public DistributedLockTemplate waitTime(long waitTime) {
        this.setWaitTime(waitTime);
        return this;
    }

    public DistributedLockTemplate timeUnit(TimeUnit waitTime) {
        this.setTimeUnit(waitTime);
        return this;
    }

    public DistributedLockTemplate leaseTime(long leaseTime) {
        this.setLeaseTime(leaseTime);
        return this;
    }

    private void validateTryAcquireLock(final RLock lock) throws InterruptedException {
        if (!tryLock(lock)) {
            throw new RuntimeException("Failed to acquire lock");
        }
    }

    public boolean tryLock(RLock lock) throws InterruptedException {
        return lock.tryLock(super.getWaitTime(), super.getLeaseTime(), super.getTimeUnit());
    }

    public RLock getDistributedLock() {
        this.validateKey();
        return this.redissonClient.getLock(getKey());
    }

    public RLock getDistributedLock(String key) {
        this.setKey(key);
        return this.getDistributedLock();
    }

    private void validateKey() {
        if (this.getKey() == null || this.getKey().isBlank()) {
            throw new IllegalArgumentException("Lock key is blank");
        }
    }

    public void release(final RLock lock) {
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
                log.info("[Thread:{}] <{}> lock released", Thread.currentThread().threadId(), lock.getName());
            } catch (Throwable e) {
                log.error("Failed to release lock", e);
            }
        }
    }
}


