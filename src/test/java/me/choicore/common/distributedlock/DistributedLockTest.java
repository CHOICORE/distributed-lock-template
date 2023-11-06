package me.choicore.common.distributedlock;

import me.choicore.example.ProductTestDataLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
        distributedLockTemplate.setKey(lockKey);
        Assertions.assertThatNoException().isThrownBy(
                () -> distributedLockTemplate.execute(() -> {
                    Assertions.assertThat(distributedLockTemplate.getKey()).isEqualTo(lockKey);
                    Assertions.assertThat(distributedLockTemplate.getDistributedLock().isLocked()).isTrue();
                }));
        Assertions.assertThat(distributedLockTemplate.getDistributedLock().isLocked()).isFalse();
    }
}
