package remote.partitioning;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(PartitionGaussWorkerConfig.class)
public class PartitionGaussWorkerStarter {
    private static final Logger log = LoggerFactory.getLogger(PartitionGaussWorkerStarter.class);

    @Test
    void test() throws InterruptedException {
        log.info("Worker is running...");
        TimeUnit.SECONDS.sleep(30);
    }
}
