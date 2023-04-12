package repository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DataSourceConfig.class)
@EnableBatchProcessing
public class HelloIncrementerConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // tag::incrementer[]
    @Bean
    RunIdIncrementer incrementer() {
        return new RunIdIncrementer();
    }

    @Bean
    Job helloJob() {
        Job job = jobBuilderFactory
                .get("helloIncrementerJob")
                .start(helloStep())
                .incrementer(incrementer()) // Bei jedem Start run.id hochzählen  
                .preventRestart() // Erneuten Start von fehlgeschlagenen Jobs verhindern
                .build();
        return job;
    }
    // end::incrementer[]

    @Bean
    Step helloStep() {
        return stepBuilderFactory.get("helloStep").tasklet(this::hello).build();
    }

    private RepeatStatus hello(StepContribution contribution, ChunkContext chunkContext) {
        System.out.println("Hello World!");
        return RepeatStatus.FINISHED;
    }
}