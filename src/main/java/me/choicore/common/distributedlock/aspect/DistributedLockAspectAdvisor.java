package me.choicore.common.distributedlock.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.choicore.common.distributedlock.DistributedLock;
import me.choicore.common.distributedlock.DistributedLockTemplate;
import me.choicore.common.distributedlock.exception.DistributedLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspectAdvisor {

    private final DistributedLockTemplate distributedLockTemplate;

    @Around("@annotation(me.choicore.common.distributedlock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) {

        final var signature = (MethodSignature) joinPoint.getSignature();
        final var method = signature.getMethod();
        final var annotation = method.getAnnotation(DistributedLock.class);

        final var lockName = DistributedLockNameGenerator.generate(
                annotation.prefix(),
                annotation.key(),
                signature.getParameterNames(),
                joinPoint.getArgs());

        return initializeDistributedLockTemplate(lockName, annotation)
                .execute(() -> {
                            try {
                                return joinPoint.proceed();
                            } catch (Throwable e) {
                                throw new DistributedLockException(e);
                            }
                        }
                );
    }

    private DistributedLockTemplate initializeDistributedLockTemplate(
            final String lockName,
            final DistributedLock distributedLock
    ) {
        return distributedLockTemplate
                .key(lockName)
                .waitTime(distributedLock.waitTime())
                .leaseTime(distributedLock.leaseTime())
                .timeUnit(distributedLock.timeUnit());
    }
}
