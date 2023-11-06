package me.choicore.common.distributedlock;

import me.choicore.example.ProductTestDataLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {ProductTestDataLoader.class})
class DistributedLockTest {

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Test
    @DisplayName("락 획득을 위한 키가 없는 경우 예외가 발생한다.")
    void t1() {
        Assertions.assertThatThrownBy(
                        () -> distributedLockTemplate.execute(() -> {
                            System.out.println("hello");
                        })
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("Lock key is blank");
    }

    @Test
    @DisplayName("잠금을 획득하고 메서드를 실행한 후 잠금을 해제한다.")
    void t2() {
        
        String lockKey = "LOCK_KEY";

        Assertions.assertThatNoException().isThrownBy(
                () -> distributedLockTemplate
                        .key(lockKey)
                        .execute(() -> {
                            System.out.println("logic");
                            Assertions.assertThat(distributedLockTemplate.getKey()).isEqualTo(lockKey);
                            Assertions.assertThat(distributedLockTemplate.getDistributedLock().isLocked()).isTrue();
                        }));
        Assertions.assertThat(distributedLockTemplate.getDistributedLock().isLocked()).isFalse();
    }

    @Test
    @DisplayName("트랜잭션 내에서 잠금을 획득하고 메서드를 실행한 후 잠금을 해제한다.")
    void t3() throws InterruptedException {

        var numberOfThreads = 20;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    distributedLockTemplate
                            .key("LOCK_KEY" + "-" + Thread.currentThread().getName())
                            .waitTime(2)
                            .leaseTime(3)
                            .withInTransaction(true)
                            .execute(() -> System.out.println("hello"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }
}
