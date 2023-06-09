package hello;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public class HelloJobManualTest {
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    // tag::code[]
    @Test
    public void testManualJobConfig() throws Exception {
        // prepare infrastructure
    	MapJobRepositoryFactoryBean jobRepositoryFactoryBean = new MapJobRepositoryFactoryBean();
        JobRepository jobRepository = jobRepositoryFactoryBean.getObject();
        PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
        jobBuilderFactory = new JobBuilderFactory(jobRepository);
        stepBuilderFactory = new StepBuilderFactory(jobRepository, transactionManager);

        // create the job
        Job job = helloJob();

        // create a launcher
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.afterPropertiesSet();

        // launch the job
        JobParameters params = new JobParameters();
        JobExecution execution = launcher.run(job, params);

        // make sure job finished successfully
        assertThat(execution.getAllFailureExceptions()).isEmpty();
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
    // end::code[]

    private Job helloJob() {
    	Tasklet tasklet = new Tasklet() {

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                System.out.println("Hello World!");
                return RepeatStatus.FINISHED;
            }
        };
        TaskletStep step = stepBuilderFactory
                .get("helloStep")
                .tasklet(tasklet)
                .build();
        Job job = jobBuilderFactory
                .get("helloJob")
                .start(step)
                .build();
        return job;
    }

}
