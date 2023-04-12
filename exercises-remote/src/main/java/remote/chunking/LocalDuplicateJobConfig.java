package remote.chunking;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import remote.common.DataSourceConfig;
import remote.duplicate.DuplicateProcessor;
import remote.duplicate.DuplicateReader;
import remote.duplicate.DuplicateWriter;

@EnableBatchProcessing
@Import(DataSourceConfig.class)
@ComponentScan(basePackageClasses = DuplicateProcessor.class)
@Configuration
public class LocalDuplicateJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DuplicateReader reader;
    @Autowired
    private DuplicateProcessor processor;
    @Autowired
    private DuplicateWriter writer;

    @Bean
    JobParametersIncrementer incrementer() {
        return new RunIdIncrementer();
    }

    @Bean
    Job localDuplicateJob() {
        return jobBuilderFactory.get("localDuplicateJob")
                .start(localDuplicateStep())
                .incrementer(incrementer())
                .build();
    }

    @Bean
    Step localDuplicateStep() {
        return stepBuilderFactory.get("localDuplicateStep")
                .<Integer, Integer>chunk(10)
                .reader(reader).processor(processor).writer(writer)
                .build();
    }
}
