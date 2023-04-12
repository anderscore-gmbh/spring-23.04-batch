package chunk.exercise;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Run with {@link org.springframework.batch.core.launch.support.CommandLineJobRunner} using parameters:
 * <i>chunk.exercise.FractionsJobConfig -next fractionsJob count(long)=100</i>.
 */
@Configuration
@EnableBatchProcessing
public class FractionsJobConfig {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	Job fractionJob() {
		return null;
	}
}
