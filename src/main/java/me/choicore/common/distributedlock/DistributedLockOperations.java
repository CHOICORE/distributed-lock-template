package me.choicore.common.distributedlock;

import java.util.Set;
import java.util.function.Supplier;

public interface DistributedLockOperations {

    <T> T execute(Supplier<T> supplier);

    <T> T execute(String prefix, Set<Long> keys, Supplier<T> supplier);

    default void execute(String prefix, Set<Long> keys, Runnable runnable) {
        execute(prefix, keys, () -> {
            runnable.run();
            return null;
        });
    }

    default void execute(Runnable runnable) {
        execute(() -> {
            runnable.run();
            return null;
        });
    }
}
