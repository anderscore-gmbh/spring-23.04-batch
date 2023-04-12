package chunk;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
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
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

@Configuration
@EnableBatchProcessing
public class RetryJobConfig extends DefaultBatchConfigurer {
    private static final Logger log = LoggerFactory.getLogger(RetryJobConfig.class);

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    ItemReader<Integer> reader() {
        return new ItemReader<Integer>() {
            private final IteratorItemReader<Integer> delegate = new IteratorItemReader<>(
                    IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList()));
            private int retry;

            @Override
            public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                Integer value = delegate.read();
                log.info("read: {}", value);
                if (Integer.valueOf(9).equals(value) && retry < 3) {
                    retry++;
                    log.info("read - retry: {}", retry);
                    // Das Retry-Template wird nur auf die Chunk-Verarbeitung (Processor, Writer)
                    // angewendet. Deswegen überspringt der Job diesen Datensatz nach dem 1. Versuch.
                    throw new MyRetryException("read");
                }
                return value;
            }
        };
    }

    @Bean
    ItemProcessor<Integer, String> processor() {
        return new ItemProcessor<Integer, String>() {
            private int retry;

            @Override
            public String process(Integer item) throws Exception {
                log.info("process: {}", item);
                if (Integer.valueOf(5).equals(item) && retry < 10) {
                    retry++;
                    log.info("process - retry: {}", retry);
                    throw new MyRetryException("process");
                }
                return "v" + item;
            }
        };
    }

    @Bean
    ItemWriter<String> writer() {
        return new ItemWriter<String>() {
            private int retry;

            @Override
            public void write(List<? extends String> items) throws Exception {
                log.info("write: {}", items);
                items.forEach(this::write);
            }

            private void write(String item) {
                if ("v7".equals(item) && retry < 3) {
                    retry++;
                    log.info("write - retry: {}", retry);
                    throw new MyRetryException("write");
                }
                log.info("written: {}", item);
            }
        };
    }

    @Bean
    SkipListener<Integer, String> loggingSkipListener() {
        return new SkipListener<Integer, String>() {

            @Override
            public void onSkipInRead(Throwable t) {
                log.warn("Skipped while reading: " + t);
            }

            @Override
            public void onSkipInProcess(Integer item, Throwable t) {
                log.warn("Skipped while processing {}: {}", item, t);
            }

            @Override
            public void onSkipInWrite(String item, Throwable t) {
                log.warn("Skipped while writing {}: {}", item, t);
            }
        };
    }

    @Bean
    TaskletStep retryStep() {
        // @formatter:off
        // tag::step[]
        TaskletStep step = stepBuilderFactory
                .get("retryStep") // StepBuilder
                .<Integer, String>chunk(4) // SimpleStepBuilder
                .reader(reader()).processor(processor()).writer(writer())
                .faultTolerant() // FaultTolerantStepBuilder
                    .retryLimit(7) // bis zu 7 mal wiederholen
                    .retry(MyRetryException.class)
                    .backOffPolicy(new ExponentialBackOffPolicy()) // nach jedem Versuch doppelt so lange Warten
                .skip(MyRetryException.class) // nach 7 Fehlversuchen diesen Datensatz überspringen
                    .skipLimit(3) // Maximal 3 Datensätze überspringen
                .listener(loggingSkipListener())
                .build();
        // end::step[]
        // @formatter:on
        return step;
    }

    @Bean
    Job retryJob() {
        // tag::job[]
        Job job = jobBuilderFactory
                .get("retryJob")
                .start(retryStep())
                .build();
        // end::job[]
        return job;
    }

}
