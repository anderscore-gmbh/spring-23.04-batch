package lesson4.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(FractionsJobConfig.class)
@ComponentScan
@EnableBatchProcessing
public class FractionsJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private Job fractionsJob;

    @Test
    void testJob() throws Exception {
    	JobParameters parameters = new JobParametersBuilder(jobExplorer)
                .addLong("count", 100L)
                .getNextJobParameters(fractionsJob)
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(fractionsJob, parameters);

        jobExecution.getAllFailureExceptions().forEach(Throwable::printStackTrace);
        assertThat(jobExecution.getExitStatus()).isEqualTo(DumpInvalidFractionsTasklet.WARNING);
    }

}
