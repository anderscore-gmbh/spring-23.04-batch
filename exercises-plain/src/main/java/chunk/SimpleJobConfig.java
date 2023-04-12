package chunk;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Job ausführen mit:
 * 
 * <pre>
 * org.springframework.batch.core.launch.support.CommandLineJobRunner chunk.SimpleJobConfig simpleJob
 * </pre>
 */
@Configuration
@EnableBatchProcessing
public class SimpleJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // tag::code[]
    @Bean
    Job simpleJob() {
        Step simpleStep = stepBuilderFactory.get("simpleStep")
                .<Integer, String>chunk(3)
                .reader(simpleReader())
                .processor(simpleProcessor())
                .writer(simpleWriter())
                .build();

        return jobBuilderFactory.get("simpleJob").start(simpleStep).build();
    }
    // end::code[]

    @Bean
    SimpleReader simpleReader() {
        return new SimpleReader(10);
    }

    @Bean
    SimpleProcessor simpleProcessor() {
        return new SimpleProcessor();
    }

    @Bean
    SimpleWriter simpleWriter() {
        return new SimpleWriter();
    }
}