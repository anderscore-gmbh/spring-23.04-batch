package chunk.exercise;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import repository.DataSourceConfig;

import java.util.Collections;
import java.util.Map;

/**
 * Run with {@link org.springframework.batch.core.launch.support.CommandLineJobRunner} using parameters:
 * <i>chunk.exercise.FractionsJobConfig -next fractionsJob count(long)=100</i>.
 */
@Configuration
@ComponentScan
@Import(DataSourceConfig.class)
@EnableBatchProcessing
public class FractionsJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private RandomFractionReader randomFractionReader;
    @Autowired
    private FractionProcessor fractionProcessor;
    @Autowired
    private SystemOutWriter systemOutWriter;

    @Autowired
    private FailedFractionReader failedFractionReader;

    @Bean
    Step listFractionsStep() {
        return stepBuilderFactory.get("listFractionsStep")
                .<Fraction, String>chunk(3)
                .reader(randomFractionReader)
                .processor(fractionProcessor)
                .writer(systemOutWriter)
                .faultTolerant().skip(ArithmeticException.class).skipLimit(10)
                .listener(failedFractionPromotionListener())
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener failedFractionPromotionListener() {
    	ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] { FractionProcessor.FAILED_FRACTIONS });
        return listener;
    }

    @Bean
    Step dumpFailedFractionsStep() {
        return stepBuilderFactory.get("dumpFailedFractionsStep")
                .<Fraction, Fraction>chunk(4)
                .reader(failedFractionReader)
                .writer(systemOutWriter)
                .build();
    }

    @Bean
    Job fractionsJob() {
        return jobBuilderFactory.get("fractionsJob")
                .start(listFractionsStep())
                .next(dumpFailedFractionsStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    ExitCodeMapper exitCodeMapper() {
    	SimpleJvmExitCodeMapper exitCodeMapper = new SimpleJvmExitCodeMapper();
        Map<String, Integer> additionalMappings = Collections.singletonMap(FailedFractionReader.WARNING.getExitCode(), 99);
        exitCodeMapper.setMapping(additionalMappings);
        return exitCodeMapper;
    }
}
