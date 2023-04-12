package hello;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

// tag::code[]
@SpringJUnitConfig(HelloConfigParamsScope.class)
public class HelloJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job helloJob;

    @Test
    void testHello() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        // run the job
        JobParameters params = new JobParametersBuilder()
                .addString("name", "Germany")
                .addLong("count", 2L)
                .toJobParameters();
        JobExecution execution = jobLauncher.run(helloJob, params);

        // make sure job finished successfully
        assertThat(execution.getAllFailureExceptions()).isEmpty();
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
}
// end::code[]
