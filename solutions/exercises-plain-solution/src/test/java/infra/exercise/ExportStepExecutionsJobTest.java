package infra.exercise;

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

@SpringJUnitConfig(ExportStepExecutionsJobConfig.class)
public class ExportStepExecutionsJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private Job exportStepExecutionsJob;

    @Autowired
    private Job exportStepExecutionsXmlJob;

    @Test
    void testExportCsvAndJson() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(exportStepExecutionsJob) // Nächste run.id beschaffen
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(exportStepExecutionsJob, jobParameters);

        System.out.println(jobExecution);
        jobExecution.getStepExecutions().forEach(System.out::println);
    }

    @Test
    void testExportXml() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(exportStepExecutionsXmlJob) // Nächste run.id beschaffen
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(exportStepExecutionsXmlJob, jobParameters);

        System.out.println(jobExecution);
        jobExecution.getStepExecutions().forEach(System.out::println);
    }

}
