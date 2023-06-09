package parallel;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(ParallelStepConfig.class)
public class ParallelStepTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Test
    void testParallel() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters());
        System.out.println(jobExecution);
        jobExecution.getStepExecutions().forEach(System.out::println);
    }

}
