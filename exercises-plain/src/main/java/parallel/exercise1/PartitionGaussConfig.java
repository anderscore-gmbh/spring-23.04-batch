package parallel.exercise1;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import repository.DataSourceConfig;

/**
 * Job ausführen mit:
 * 
 * <pre>
 * org.springframework.batch.core.launch.support.CommandLineJobRunner 
 *    parallel.exercise1.PartitionGaussConfig -next partitionJob minValue(long)=1 maxValue(long)=100
 * </pre>
 */
@Configuration
@ComponentScan
@Import(DataSourceConfig.class)
@EnableBatchProcessing
public class PartitionGaussConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private NumberRangePartitioner numberRangePartitioner;
    @Autowired
    private NumberRangeReader numberRangeReader;
    @Autowired
    private SumItemWriter sumItemWriter;
    @Autowired
    private SumAggregator sumAggregator;
    @Autowired
    private ShowResultTasklet showResultTasklet;

    /**
     * Dieser Job berechnet die Summe der Zahlen zwischen 1 und 100. Vorgehen: 1. Aufteilen in 20er Pakete und von 5
     * Threads
     * parallel verarbeiten. 2. Teilergebnisse aggregieren und im ExecutionContext ablegen 3. Weiterer Step gibt
     * Ergebnis
     * aus.
     */
    @Bean
    Job partitionJob() {
        return null;
    }
}
