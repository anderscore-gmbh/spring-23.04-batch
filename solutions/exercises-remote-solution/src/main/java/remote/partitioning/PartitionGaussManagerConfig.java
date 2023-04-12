package remote.partitioning;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.listener.JobParameterExecutionContextCopyListener;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import remote.common.ActiveMQConfig;
import remote.common.DataSourceConfig;
import remote.gauss.NumberRangePartitioner;
import remote.gauss.ShowResultTasklet;
import remote.gauss.SumAggregator;

/**
 * Job ausführen mit:
 * 
 * <pre>
 * org.springframework.batch.core.launch.support.CommandLineJobRunner 
 *    remote.partitioning.PartitionGaussManagerConfig -next partitionJob minValue(long)=1 maxValue(long)=100
 * </pre>
 */
@Configuration
@Import({ DataSourceConfig.class, ActiveMQConfig.class })
@ComponentScan(basePackageClasses = NumberRangePartitioner.class)
@EnableBatchProcessing
@EnableBatchIntegration
public class PartitionGaussManagerConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private RemotePartitioningManagerStepBuilderFactory managerStepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

    @Autowired
    private NumberRangePartitioner numberRangePartitioner;
    @Autowired
    private SumAggregator sumAggregator;
    @Autowired
    private ShowResultTasklet showResultTasklet;

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

    /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow() {
        return IntegrationFlows
                .from(requests())
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destination("gauss.requests"))
                .get();
    }

    /*
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow() {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination("gauss.replies"))
                .channel(replies())
                .get();
    }

    @Bean
    Step managerStep() {
        Step step = managerStepBuilderFactory
                .get("managerStep")
                .listener(parameterCopyListener())
                .listener(promotionListener())
                .partitioner("workerStep", numberRangePartitioner)
                .outputChannel(requests())
                .inputChannel(replies())
                .aggregator(sumAggregator)
                .build();
        return step;
    }

    @Bean
    Job remotePartitioningJob() {
        TaskletStep resultStep = stepBuilderFactory
                .get("showResult")
                .tasklet(showResultTasklet)
                .build();

        Job job = jobBuilderFactory
                .get("remotePartitioningJob")
                .start(managerStep())
                .next(resultStep)
                .incrementer(incementer())
                .build();
        return job;
    }
}
