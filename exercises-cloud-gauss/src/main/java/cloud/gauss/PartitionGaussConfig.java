package cloud.gauss;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.listener.JobParameterExecutionContextCopyListener;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Import(DeployerPartitionHandlerConfig.class)
public class PartitionGaussConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private NumberRangePartitioner numberRangePartitioner;
    @Autowired
    private NumberRangeReader numberRangeReader;
    @Autowired
    private SumItemWriter sumItemWriter;
    @Autowired
    private SumAggregator sumAggregator;
    @Autowired
    private ShowResultTasklet showResultTasklet;

    @Autowired
    private PartitionHandler partitionHandler;

    @Bean
    RunIdIncrementer incementer() {
        return new RunIdIncrementer();
    }

    /**
     * Ergebnis aus Step-ExecutionContext in den Job-ExecutionContext kopieren
     */
    @Bean
    ExecutionContextPromotionListener promotionListener() {
    	ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] { "sum" });
        return listener;
    }

    /**
     * Parameter in den Step-ExecutionContext kopieren
     */
    @Bean
    JobParameterExecutionContextCopyListener parameterCopyListener() {
    	JobParameterExecutionContextCopyListener listener = new JobParameterExecutionContextCopyListener();
        listener.setKeys(new String[] { "minValue", "maxValue" });
        return listener;
    }

    @Bean
    ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        return taskExecutor;
    }

    @Bean
    TaskletStep workerStep() {
        TaskletStep step = stepBuilderFactory
                .get("workerStep")
                .<Integer, Integer>chunk(10)
                .reader(numberRangeReader).writer(sumItemWriter) // Prozessor ist optional
                .stream(numberRangeReader) // Ruft bei Reader open() und close() auf (hier nicht notwendig)
                .build();
        return step;
    }

    /**
     * Step, der die Partitionierung für einen anderen Step ausführt
     */
    @Bean
    Step partitionStep() {
        Step partitionStep = stepBuilderFactory.get("partitionStep")
                .listener(parameterCopyListener()) // Parameter in Step-ExecutionContxt kopieren
                .listener(promotionListener()) // Summe aus Step-ExecutionContext in Job-ExcecutionContext kopieren
                .partitioner("stepx", numberRangePartitioner) // Präfix für partitionierte Steps
                .step(workerStep()) // Der Step, der von meheren Threads ausgeführt wird
                .gridSize(5) // Anzahl Threads
                .taskExecutor(taskExecutor())
                .aggregator(sumAggregator)
                .partitionHandler(partitionHandler)
                .build();
        return partitionStep;
    }

    /**
     * Dieser Job berechnet die Summe der Zahlen zwischen 1 und 100. Vorgehen: 1. Aufteilen in 20er Pakete und von 5
     * Threads
     * parallel verarbeiten. 2. Teilergebnisse aggregieren und im ExecutionContext ablegen 3. Weiterer Step gibt
     * Ergebnis
     * aus.
     */
    @Bean
    @Profile("!worker")
    Job partitionJob() {
        TaskletStep resultStep = stepBuilderFactory
                .get("showResult")
                .tasklet(showResultTasklet)
                .build();

        Job job = jobBuilderFactory
                .get("partitionJob")
                .start(partitionStep())
                .next(resultStep)
                .incrementer(incementer())
                .build();
        return job;
    }
}
