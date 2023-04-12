package de.springbatchdeepdive.hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// tag::config[]
@Configuration
@EnableBatchProcessing
public class HelloConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
// end::config[]

    // tag::job[]
    @Bean
    HelloTasklet helloTasklet() {
        return new HelloTasklet();
    }

    @Bean
    Step helloStep() {
        return stepBuilderFactory.get("helloStep").tasklet(helloTasklet()).build();
    }

    @Bean
    Job helloJob() {
        return jobBuilderFactory.get("helloJob").start(helloStep()).build();
    }
    // end::job[]

// tag::config[]

    // ...

}
// end::config[]
