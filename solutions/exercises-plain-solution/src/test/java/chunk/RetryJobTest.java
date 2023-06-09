package chunk;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(RetryJobConfig.class)
public class RetryJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job retryJob;

    @Test
    void testRetry() throws Exception {
        jobLauncher.run(retryJob, new JobParameters());
    }

}
