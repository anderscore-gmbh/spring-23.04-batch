package infra.exercise;

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

@SpringJUnitConfig(ExportStepExecutionsJobConfig.class)
public class ExportStepExecutionsJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;
    
    @Autowired
    private Job dumpStepExecutionDataJob;
    
    @Test
    void testJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
        		.getNextJobParameters(dumpStepExecutionDataJob)
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(dumpStepExecutionDataJob, jobParameters);
        
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }


}
