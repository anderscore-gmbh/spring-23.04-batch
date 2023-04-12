package repository;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

// tag::code[]
@SpringJUnitConfig(HelloRepositoryConfig.class)
public class JobExplorerSample {

    @Autowired
    private JobExplorer jobExplorer;

    // end::code[]
    @Test
    public void testDumpJobExecutionAndStepExecutions() {
        for (String jobName : jobExplorer.getJobNames()) {
            for (JobInstance jobInstance : jobExplorer.getJobInstances(jobName, 0, 100)) {
                for (JobExecution jobExecution : jobExplorer.getJobExecutions(jobInstance)) {
                    System.out.printf("%-30s %tc %-10s %-10s%n", jobName,
                            jobExecution.getStartTime(),
                            jobExecution.getStatus(),
                            jobExecution.getExitStatus().getExitCode());
                    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                        System.out.printf("          %-20s %tc %-10s %-10s%n", stepExecution.getStepName(),
                                stepExecution.getStartTime(),
                                stepExecution.getStatus(),
                                stepExecution.getExitStatus().getExitCode());
                    }
                }
            }
        }
    }
    // tag::code[]
}
// end::code[]
