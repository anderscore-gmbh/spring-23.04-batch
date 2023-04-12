package parallel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(FlowJobConfig.class)
public class FlowJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job flowJob;

    @Test
    void testJob() throws Exception {
        JobExecution jobExecution = jobLauncher.run(flowJob, new JobParameters());
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }

}
