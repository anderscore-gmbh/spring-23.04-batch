package ops.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
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
import org.springframework.scheduling.quartz.QuartzJobBean;

// tag::code[]
public class HelloQuartzJob extends QuartzJobBean {
    private static final Logger log = LoggerFactory.getLogger(HelloQuartzJob.class);

    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job helloQuartzJob;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .addDate("scheduled.at", context.getScheduledFireTime()) // wird im Cluster nur einmal ausgeführt
                .toJobParameters();
        try {
            JobExecution jobExecution = jobLauncher.run(helloQuartzJob, jobParameters);
            log.info("HelloQuartzJob finished: {}", jobExecution);
            if (!ExitStatus.COMPLETED.getExitCode().equals(jobExecution.getExitStatus().getExitCode())) {
                throw new JobExecutionException("HelloQuartzJob ended with " + jobExecution.getExitStatus());
            }
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException ex) {
            log.error("HelloQuartzJob failed.", ex);
            throw new JobExecutionException("HelloQuartzJob failed", ex);
        }
    }
}
// end::code[]
