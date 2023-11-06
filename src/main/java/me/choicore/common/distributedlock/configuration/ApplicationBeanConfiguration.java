package me.choicore.common.distributedlock.configuration;

import me.choicore.common.distributedlock.DefaultDistributedLockDefinition;
import me.choicore.common.distributedlock.DistributedLockDefinition;
import me.choicore.common.distributedlock.DistributedLockProperties;
import me.choicore.common.distributedlock.DistributedLockTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration(proxyBeanMethods = false)
public class ApplicationBeanConfiguration {

    @Bean
    public DistributedLockDefinition distributedLockDefinition(DistributedLockProperties distributedLockProperties) {
        return new DefaultDistributedLockDefinition(
                distributedLockProperties.waitTime(),
                distributedLockProperties.leaseTime(),
                distributedLockProperties.timeUnit()
        );
    }

    @Bean
    public DistributedLockTemplate distributedLockTemplate(
            RedissonClient redissonClient,
            DistributedLockDefinition distributedLockDefinition,
            TransactionTemplate transactionTemplate
    ) {
        return new DistributedLockTemplate(redissonClient, distributedLockDefinition, transactionTemplate);
    }
}
