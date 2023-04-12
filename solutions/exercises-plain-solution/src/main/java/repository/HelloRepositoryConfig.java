package repository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DataSourceConfig.class)
@EnableBatchProcessing
public class HelloRepositoryConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    Job helloJob() {
        return jobBuilderFactory.get("helloJob").start(helloStep()).build();
    }

    @Bean
    Step helloStep() {
        return stepBuilderFactory.get("helloStep").tasklet(helloTasklet()).build();
    }

    @Bean
    @JobScope
    Tasklet helloTasklet() {
        return new Tasklet() {
            // tag::el[]
            @Value("#{jobParameters[name]?:'World'}")
            private String name;
            @Value("#{jobParameters[count]?:1}")
            private int count;
            // end::el[]

            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                System.out.printf("Hello %s!%n", name);
                count--;
                return count > 0 ? RepeatStatus.CONTINUABLE : RepeatStatus.FINISHED;
            }
        };
    }
}