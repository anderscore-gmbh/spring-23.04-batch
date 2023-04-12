package parallel.exercise2;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
    ProducerConsumerQueue<Integer> queue() {
    	return new ProducerConsumerQueue<Integer>(20, -1);
    }
    
    @Bean
    Step producerStep() {
    	return stepBuilderFactory.get("producerStep")
    			.<Integer, Integer>chunk(10)
    			.reader(reader)
    			.writer(queue())
    			.build();
    }
    
    @Bean
    Step consumerStep() {
        return stepBuilderFactory.get("consumerStep")
                .<Integer, String>chunk(5)
                .reader(queue())
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .throttleLimit(8)
                .build();
    }

    @Bean
    Job producerConsumerJob() {
        Flow producerFlow = new FlowBuilder<Flow>("producerFlow").from(producerStep()).build();
        Flow consumerFlow = new FlowBuilder<Flow>("consumerFlow").from(consumerStep()).build();
        Flow splitFlow = new FlowBuilder<Flow>("splitFlow")
                .split(taskExecutor())
                .add(producerFlow, consumerFlow).build();
        return jobBuilderFactory.get("parallelJob").start(splitFlow).end().incrementer(incrementer()).build();
    }
}
