package infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {
        PersonDumpConfig.class,
        PersonXmlJobConfig.class,
        PersonJsonJacksonJobConfig.class,
        PersonJsonGsonJobConfig.class })
public class PersonJobsTest {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job personDumpJob;

    @Autowired
    private Job personXmlJob;

    @Autowired
    private Job personJacksonJob;

    @Autowired
    private Job personGsonJob;

    @Test
    void testPersonDumpJob() throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException, IOException {
        assertThat(personDumpJob.getName()).isEqualTo("personDumpJob");
        checkResult(jobLauncher.run(personDumpJob, fileParam("Persons.csv")), 3, 0, 3);
    }

    @Test
    void testPersonXmlJob() throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException, IOException {
        assertThat(personXmlJob.getName()).isEqualTo("personXmlJob");
        checkResult(jobLauncher.run(personXmlJob, fileParam("Persons.xml")), 3, 1, 2);
    }

    @Test
    void testPersonJacksonJob() throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException, IOException {
        assertThat(personJacksonJob.getName()).isEqualTo("personJacksonJob");
        checkResult(jobLauncher.run(personJacksonJob, fileParam("Persons.json")), 4, 1, 3);
    }

    @Test
    void testPersonGsonJob() throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException, IOException {
        assertThat(personGsonJob.getName()).isEqualTo("personGsonJob");
        checkResult(jobLauncher.run(personGsonJob, fileParam("Persons.json")), 4, 1, 3);
    }

    private void checkResult(JobExecution jobExecution,
            int expectedReadCount, int expectedFilterCount, int expectedWriteCount) {
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount()).isEqualTo(expectedReadCount);
        assertThat(stepExecution.getFilterCount()).isEqualTo(expectedFilterCount);
        assertThat(stepExecution.getWriteCount()).isEqualTo(expectedWriteCount);
    }

    private JobParameters fileParam(String resourceName) throws IOException {
        String resourceUri = new ClassPathResource(resourceName).getURI().toASCIIString();
        JobParameters params = new JobParametersBuilder()
                .addString("file", resourceUri)
                .toJobParameters();
        return params;
    }
}
