package infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class FilteringTest {

    @Configuration
    @EnableBatchProcessing
    static class Config {
        @Autowired
        private JobBuilderFactory jobBuilderFactory;
        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        ItemReader<Integer> reader() {
            return new IteratorItemReader<>(IntStream.rangeClosed(1, 100).iterator());
        }

        @Bean
        ItemProcessor<Integer, Integer> processor() {
            return v -> v.intValue() % 5 == 0 ? null : v;
        }

        @Bean
        ItemWriter<Integer> writer() {
            return System.out::println;
        }

        @Bean
        Step filterStep() {
            return stepBuilderFactory.get("filterStep")
                    .<Integer, Integer>chunk(10)
                    .reader(reader()).processor(processor()).writer(writer())
                    .build();
        }

        @Bean
        Job filterJob() {
            return jobBuilderFactory.get("filterJob").start(filterStep()).build();
        }
    }

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Test
    void test() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
            JobParametersInvalidException {
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

        System.out.println(jobExecution);
        jobExecution.getStepExecutions().forEach(System.out::println);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getReadCount()).isEqualTo(100);
        assertThat(stepExecution.getWriteCount()).isEqualTo(80);
        assertThat(stepExecution.getFilterCount()).isEqualTo(20);
    }
}
