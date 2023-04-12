package remote.chunking;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
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
import remote.duplicate.DuplicateProcessor;
import remote.duplicate.DuplicateWriter;

@Configuration
@Import({ DataSourceConfig.class, ActiveMQConfig.class })
@ComponentScan(basePackageClasses = DuplicateProcessor.class)
@EnableBatchProcessing
@EnableBatchIntegration
public class DuplicateWorkerConfig {

    @Autowired
    private RemoteChunkingWorkerBuilder<Integer, Integer> remoteChunkingWorkerBuilder;

    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

    @Autowired
    private DuplicateProcessor processor;
    @Autowired
    private DuplicateWriter writer;

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
                        .destination("sample.requests"))
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
                        .destination("sample.replies"))
                .get();
    }

    /*
     * Configure worker components
     */
    // tag::flow[]
    @Bean
    public IntegrationFlow workerIntegrationFlow() {
        return this.remoteChunkingWorkerBuilder
                .itemProcessor(processor)
                .itemWriter(writer)
                .inputChannel(requests())
                .outputChannel(replies())
                .build();
    }
    // end::flow[]
}
