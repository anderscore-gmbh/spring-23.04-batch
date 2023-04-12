package remote.chunking;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import remote.common.ActiveMQConfig;
import remote.common.DataSourceConfig;
import remote.duplicate.DuplicateProcessor;
import remote.duplicate.DuplicateReader;

@Configuration
@Import({ DataSourceConfig.class, ActiveMQConfig.class })
@ComponentScan(basePackageClasses = DuplicateProcessor.class)
@EnableBatchProcessing
@EnableBatchIntegration
public class DuplicateManagerConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private RemoteChunkingManagerStepBuilderFactory managerStepBuilderFactory;

    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

    @Autowired
    private DuplicateReader reader;

    /*
     * Configure outbound flow (requests going to workers)
     */
    // tag::outbound[]
    @Bean
    DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow() {
        return IntegrationFlows
                .from(requests())
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destination("sample.requests"))
                .get();
    }
    // end::outbound[]

    /*
     * Configure inbound flow (replies coming from workers)
     */
    // tag::inbound[]
    @Bean
    public QueueChannel replies() {
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow() {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination("sample.replies"))
                .channel(replies())
                .get();
    }
    // end::inbound[]

    /*
     * Configure master step components
     */
    @Bean
    JobParametersIncrementer incrementer() {
        return new RunIdIncrementer();
    }

    // tag::step[]
    @Bean
    public TaskletStep managerStep() {
        return this.managerStepBuilderFactory.get("managerStep")
                .chunk(10)
                .reader(reader)
                .outputChannel(requests())
                .inputChannel(replies())
                .build();
    }
    // end::step[]

    @Bean
    public Job remoteChunkingJob() {
        return this.jobBuilderFactory.get("remoteChunkingJob")
                .start(managerStep())
                .incrementer(incrementer())
                .build();
    }

}
