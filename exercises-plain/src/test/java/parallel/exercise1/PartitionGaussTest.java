package parallel.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(PartitionGaussConfig.class)
public class PartitionGaussTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private Job job;

    @Test
    void testJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(job) // Nächste run.id beschaffen
                .addLong("minValue", 1L)
                .addLong("maxValue", 1000L)
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        System.out.println(jobExecution);
        jobExecution.getStepExecutions().forEach(System.out::println);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(jobExecution.getExecutionContext().get("sum")).isEqualTo(500500);
    }

}
