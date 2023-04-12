package hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@EnableBatchProcessing
public class HelloConfigParamsListener {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    Job helloJob(Step helloStep, HelloTasklet helloTasklet) {
        // tag::registerListener[]
        return jobBuilderFactory.get("helloJob")
                .start(helloStep)
                .listener(helloTasklet) // Tasklet als JobExecutionListener registrieren
                .build();
        // end::registerListener[]
    }

    @Bean
    Step helloStep(Tasklet helloTasklet) {
        return stepBuilderFactory.get("helloStep").tasklet(helloTasklet).build();
    }

    @Component
    static class HelloTasklet extends JobExecutionListenerSupport implements Tasklet {
        private String name;
        private int count;

        @Override
        public void beforeJob(JobExecution jobExecution) {
            name = jobExecution.getJobParameters().getString("name", "World");
            count = jobExecution.getJobParameters().getLong("count", Long.valueOf(1)).intValue();
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            System.out.printf("Hello %s!%n", name);
            count--;
            return RepeatStatus.continueIf(count > 0);
        }
    }

}