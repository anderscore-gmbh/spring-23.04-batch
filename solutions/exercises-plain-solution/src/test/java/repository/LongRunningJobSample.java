package repository;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(LongRunnigJobConfig.class)
public class LongRunningJobSample {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private Job longRunningJob;

    @Test
    void testLaunchJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(longRunningJob)
                .addLong("stop.at", 30L)
                .addLong("fail.at", 10L)
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(longRunningJob, jobParameters);

        System.out.println(jobExecution);
        jobExecution.getStepExecutions().forEach(System.out::println);
        jobExecution.getAllFailureExceptions().forEach(System.out::println);
    }
}
