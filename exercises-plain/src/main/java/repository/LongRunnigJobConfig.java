package repository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@Import(DataSourceConfig.class)
@EnableBatchProcessing
public class LongRunnigJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    Job longRunningJob() {
        return jobBuilderFactory
                .get("longRunningJob")
                .start(longRunningStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step longRunningStep() {
        return stepBuilderFactory.get("longRunningStep")
                .<Instant, Instant>chunk(1)
                .reader(reader())
                .writer(this::write)
                .build();
    }

    @Bean
    @JobScope
    ItemReader<Instant> reader() {
        return new ItemReader<Instant>() {
            @Value("#{jobParameters['stop.at']?:300}")
            private int stopAt;
            @Value("#{jobParameters['fail.at']?:-1}")
            private int failAt;

            private int count;

            @Override
            public Instant read() throws InterruptedException {
                if (count == failAt) {
                    throw new RuntimeException("Failure at " + failAt);
                }
                if (count >= stopAt) {
                    return null;
                }
                count++;
                TimeUnit.SECONDS.sleep(1);
                return Instant.now();
            }
        };
    }

    private void write(List<? extends Instant> instants) {
        instants.forEach(System.out::println);
    }
}
