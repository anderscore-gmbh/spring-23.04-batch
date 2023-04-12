package remote.chunking;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(DuplicateWorkerConfig.class)
public class DuplicateWorkerStarter {

    private static final Logger log = LoggerFactory.getLogger(DuplicateManagerStarter.class);

    @Test
    void test() throws InterruptedException {
        log.info("Worker is running...");
        TimeUnit.SECONDS.sleep(300);
    }
}
