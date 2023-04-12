package ops.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;

@Configuration
@EnableBatchProcessing
// tag::code[]
@EnableScheduling
public class HelloScheduledConfig {
    // end::code[]
    private static final Logger log = LoggerFactory.getLogger(HelloScheduledConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobLauncher jobLauncher;

    @Bean
    Job helloScheduledJob() {
        Step step = stepBuilderFactory.get("helloScheduledStep").tasklet(this::sayHello).build();
        return jobBuilderFactory.get("helloScheduledJob").start(step)
                .incrementer(new RunIdIncrementer()).build();
    }

    RepeatStatus sayHello(StepContribution contribution, ChunkContext chunkContext) {
        System.out.println("Hello Scheduled!");
        return RepeatStatus.FINISHED;
    }

    /**
     * Execute the job at the scheduled time. The scheduled time is given by a cron expression defined in the
     * 'ops.hello.schedule.cron' application.properties entry. See {@link CronSequenceGenerator} for an explanation to
     * the
     * cron format used by Spring.
     */
    // tag::code[]
    @Scheduled(cron = "${ops.hello.schedule.cron:-}")
    void executeHelloJob() {
        // end::code[]
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(helloScheduledJob())
                .toJobParameters();
        try {
            log.info("Starting helloScheduledJob job...");
            JobExecution jobExecution = jobLauncher.run(helloScheduledJob(), jobParameters);
            log.info("HelloScheduledJob job finished: {}", jobExecution);
            if (log.isDebugEnabled()) {
                jobExecution.getStepExecutions().forEach(se -> log.debug("step execution: {}", se));
            }
            jobExecution.getFailureExceptions().forEach(th -> log.warn("Failure during job execution:", th));
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException ex) {
            log.error("Failed to execute helloScheduledJob job", ex);
        }
        // tag::code[]
        // ...
    }
}
// end::code[]
