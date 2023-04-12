package parallel;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
public class ParallelStepConfig {
    private static final Logger log = LoggerFactory.getLogger(ParallelStepConfig.class);

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    // tag::executor[]
    @Bean
    ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        return taskExecutor;
    }
    // end::executor[]

    @Bean
    ItemReader<Integer> reader() {
        return new ItemReader<Integer>() {
            private final IteratorItemReader<Integer> delegate = new IteratorItemReader<>(
                    IntStream.rangeClosed(1, 50).boxed().collect(Collectors.toList()));

            @Override
            public synchronized Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                Integer value = delegate.read();
                TimeUnit.MILLISECONDS.sleep(100);
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

    // tag::step[]
    @Bean
    TaskletStep parallelStep() {
        TaskletStep step = stepBuilderFactory
                .get("parallelStep")
                .<Integer, String>chunk(5)
                .reader(reader()).processor(processor()).writer(writer())
                .taskExecutor(taskExecutor()) // Ausführung mit mehreren Threads
                .throttleLimit(4) // begrenzt Anzahl der Threads (default: 4)
                .build();
        return step;
    }
    // end::step[]

    @Bean
    Job parallelJob(Step step) {
        Job job = jobBuilderFactory
                .get("parallelJob")
                .start(step)
                .build();
        return job;
    }
}
