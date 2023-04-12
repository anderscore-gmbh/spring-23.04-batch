package de.springbatchdeepdive.hello;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(HelloConfig.class)
class HelloJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job helloJob;

    @Test
    void test() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters params = new JobParametersBuilder()
                .addString("name", "Egon")
                .addLong("count", 5L)
                .toJobParameters();
        JobExecution execution = jobLauncher.run(helloJob, params);
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
}