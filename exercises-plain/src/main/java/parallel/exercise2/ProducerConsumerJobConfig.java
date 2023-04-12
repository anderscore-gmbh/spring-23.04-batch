package parallel.exercise2;

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
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import repository.DataSourceConfig;

/**
 * Job ausführen mit:
 * 
 * <pre>
 * org.springframework.batch.core.launch.support.CommandLineJobRunner 
 *    parallel.exercise2.ProducerConsumerJobConfig -next producerConsumerJob count(long)=1000
 * </pre>
 */
@Configuration
@EnableBatchProcessing
@Import(DataSourceConfig.class)
@ComponentScan
public class ProducerConsumerJobConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private NumberReader reader;
    @Autowired
    private SlowProcessor processor;
    @Autowired
    private LogWriter<String> writer;

    @Bean
    JobParametersIncrementer incrementer() {
        return new RunIdIncrementer();
    }

    @Bean
    TaskExecutor taskExecutor() {
    	ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(12);
        return taskExecutor;
    }

    @Bean
    Job simpleJob() {
        Step simpleStep = stepBuilderFactory.get("simpleStep")
                .<Integer, String>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();

        return jobBuilderFactory.get("simpleJob").start(simpleStep).incrementer(incrementer()).build();
    }
}
