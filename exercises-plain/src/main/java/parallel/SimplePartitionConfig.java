package parallel;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
public class SimplePartitionConfig extends DefaultBatchConfigurer {
    private static final Logger log = LoggerFactory.getLogger(SimplePartitionConfig.class);

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        return taskExecutor;
    }

    @Bean
    ItemReader<Integer> reader() {
        return new ItemReader<Integer>() {
            private final IteratorItemReader<Integer> delegate = new IteratorItemReader<>(
                    IntStream.rangeClosed(1, 50).boxed().collect(Collectors.toList()));

            @Override
            public synchronized Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                Integer value = delegate.read();
                log.debug("read: {}", value);
                return value;
            }
        };
    }

    @Bean
    ItemProcessor<Integer, String> processor() {
        return new ItemProcessor<Integer, String>() {

            @Override
            public String process(Integer item) throws Exception {
                log.debug("process: {}", item);
                TimeUnit.MILLISECONDS.sleep(100);
                return "v" + item;
            }
        };
    }

    @Bean
    ItemWriter<String> writer() {
        return new ItemWriter<String>() {

            @Override
            public void write(List<? extends String> items) throws Exception {
                log.info("write: {}", items);
            }
        };
    }

    @Bean
    TaskletStep step() {
        // tag::step[]
        return stepBuilderFactory
                .get("simpleStep")
                .<Integer, String>chunk(4)
                .reader(reader()).processor(processor()).writer(writer())
                .build();
        // end::step[]
    }

    @Bean
    Step partitionStep() {
        // tag::partition[]
        return stepBuilderFactory.get("partitionStep")
                .partitioner("stepx", new SimplePartitioner()) // Präfix für partitionierte Steps
                .step(step()) // Der Step, der von meheren Threads ausgeführt wird
                .gridSize(5) // Anzahl Threads
                .taskExecutor(taskExecutor())
                .build();
        // end::partition[]
    }

    @Bean
    Job partitionJob() {
        // tag::job[]
        return jobBuilderFactory
                .get("partitionJob")
                .start(partitionStep())
                .build();
        // end::job[]
    }

}
