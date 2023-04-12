package remote.partitioning;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
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
import remote.gauss.NumberRangeReader;
import remote.gauss.SumItemWriter;

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
public class PartitionGaussWorkerConfig {

    @Autowired
    private RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

    @Autowired
    private NumberRangeReader numberRangeReader;
    @Autowired
    private SumItemWriter sumItemWriter;

    /*
     * Configure inbound flow (requests coming from the master)
     */
    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow() {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination("gauss.requests"))
                .channel(requests())
                .get();
    }

    /*
     * Configure outbound flow (replies going to the master)
     */
    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow() {
        return IntegrationFlows
                .from(replies())
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destination("gauss.replies"))
                .get();
    }

    @Bean
    Step workerStep() {
        Step step = workerStepBuilderFactory
                .get("workerStep")
                .inputChannel(requests())
                .outputChannel(replies())
                .<Integer, Integer>chunk(10)
                .reader(numberRangeReader)
                .writer(sumItemWriter)
                .stream(numberRangeReader)
                .build();
        return step;
    }
}
