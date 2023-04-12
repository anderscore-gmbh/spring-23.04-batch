package lesson4.exercise;

import java.util.Collections;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Run with {@link org.springframework.batch.core.launch.support.CommandLineJobRunner} using parameters:
 * <i>chunk.exercise.FractionsJobConfig -next fractionsJob count(long)=100</i>.
 */
@Configuration
public class FractionsJobConfig {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private FractionReader fractionReader;

	@Autowired
	private FractionProcessor fractionProcessor;

	@Autowired
	private FractionWriter fractionWriter;
	
	@Autowired
	private DumpInvalidFractionsTasklet dumpInvalidTasklet;
	
	@Bean
	Step fractionStep() {
		return stepBuilderFactory.get("fractionStep")
				.<Fraction, String>chunk(5)
				.reader(fractionReader)
				.processor(fractionProcessor)
				.writer(fractionWriter)
				.faultTolerant()
				.skip(ArithmeticException.class)
				.skipLimit(50)
				.listener(promotionListener())
				.build();
	}
	
	@Bean
	Step dumpStep() {
		return stepBuilderFactory.get("dumpStep")
				.tasklet(dumpInvalidTasklet)
				.listener(dumpInvalidTasklet)
				.build();
	}

	@Bean
	RunIdIncrementer incrementer() {
		return new RunIdIncrementer();
	}
	
	@Bean
	Job fractionJob() {
		return jobBuilderFactory.get("fractionJob")
				.start(fractionStep())
				.next(dumpStep())
				.incrementer(incrementer())
				.listener(dumpException())
				.build();
	}

	@Bean
	ExecutionContextPromotionListener promotionListener() {
		ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] {"invalidFractions"});
		return listener;
	}
	
	@Bean
	JobExecutionListener dumpException() {
		return new JobExecutionListenerSupport() {

			@Override
			public void afterJob(JobExecution jobExecution) {
				jobExecution.getAllFailureExceptions().forEach(System.out::println);
			}
		};
	}
	
    @Bean
    ExitCodeMapper exitCodeMapper() {
    	SimpleJvmExitCodeMapper exitCodeMapper = new SimpleJvmExitCodeMapper();
        Map<String, Integer> additionalMappings = Collections.singletonMap(DumpInvalidFractionsTasklet.WARNING.getExitCode(), 99);
        exitCodeMapper.setMapping(additionalMappings);
        return exitCodeMapper;
    }

}
