package chunk;

import java.math.BigDecimal;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import infra.SystemOutWriter;

/**
 * Job ausführen mit:
 * 
 * <pre>
 * org.springframework.batch.core.launch.support.CommandLineJobRunner chunk.SimpleJobConfig simpleJob
 * </pre>
 */
@Configuration
@EnableBatchProcessing
public class DelegateJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // tag::code[]
    @Bean
    Job delegateJob() {
        Step delegateStep = stepBuilderFactory.get("delegateStep")
                .<BigDecimal, BigDecimal>chunk(1)
                .reader(delegateReader())
                .writer(systemOutWriter())
                .build();

        return jobBuilderFactory.get("delegateJob").start(delegateStep).build();
    }
    // end::code[]

    @Bean
    SimpleReader simpleReader() {
        return new SimpleReader(10);
    }

    DelegateReader delegateReader() {
        return new DelegateReader(simpleReader());
    }

    @Bean
    SystemOutWriter<BigDecimal> systemOutWriter() {
        return new SystemOutWriter<BigDecimal>();
    }
}