package ops.quartz;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class HelloQuartzConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    Job helloQuartzJob() {
        Step step = stepBuilderFactory.get("helloQuartzStep").tasklet(this::sayHello).build();
        return jobBuilderFactory.get("helloQuartzJob").start(step).build();
    }

    RepeatStatus sayHello(StepContribution contribution, ChunkContext chunkContext) {
        System.out.println("Hello Quartz!");
        return RepeatStatus.FINISHED;
    }
}
