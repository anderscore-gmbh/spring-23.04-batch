package chunk.exercise;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(FractionsJobConfig.class)
public class FractionsJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job fractionsJob;

    @Test
    void testJob() throws Exception {
    	JobParameters parameters = new JobParametersBuilder()
                .addLong("count", 1000L)
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(fractionsJob, parameters);

        jobExecution.getAllFailureExceptions().forEach(Throwable::printStackTrace);
    }

}
