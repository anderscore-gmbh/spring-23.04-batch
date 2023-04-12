package ops.integration;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.file.dsl.FileInboundChannelAdapterSpec;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.handler.LoggingHandler;

@Configuration
@EnableBatchProcessing
@EnableIntegration
public class FilePollerJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobRepository jobRepository;

    // tag::reader[]
    @Bean
    @StepScope
    ResourcesItemReader filePollerReader(@Value("#{jobParameters['input.file.name']}") String resource) {
    	ResourcesItemReader reader = new ResourcesItemReader();
        reader.setResources(new Resource[] { new FileSystemResource(resource) });
        return reader;
    }
    // end::reader[]

    @Bean
    ItemProcessor<Resource, Resource> deleteFileProcessor() {
        return resource -> {
            resource.getFile().delete(); // Datei löschen damit nicht ständig die gleiche Message verschickt wird
            return resource;
        };
    }

    @Bean
    ItemWriter<Resource> fileListerWriter() {
        return resources -> resources.forEach(resource -> System.out.printf("Processed: %s%n", resource.getFilename()));
    }

    @Bean
    Job filePollerJob(ItemReader<Resource> filePollerReader) {
        TaskletStep step = stepBuilderFactory.get("filePollerStep")
                .<Resource, Resource>chunk(1)
                .reader(filePollerReader).processor(deleteFileProcessor()).writer(fileListerWriter())
                .build();

        return jobBuilderFactory.get("filePollerJob").start(step).build();
    }

    // tag::transformer[]
    @Bean
    public FileMessageToJobRequest fileMessageToJobRequest(Job filePollerJob) {
        FileMessageToJobRequest fileMessageToJobRequest = new FileMessageToJobRequest();
        fileMessageToJobRequest.setFileParameterName("input.file.name");
        fileMessageToJobRequest.setJob(filePollerJob);
        return fileMessageToJobRequest;
    }
    // end::transformer[]

    // tag::gateway[]
    @Bean
    public JobLaunchingGateway jobLaunchingGateway() {
        SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        simpleJobLauncher.setTaskExecutor(new SyncTaskExecutor());
        JobLaunchingGateway jobLaunchingGateway = new JobLaunchingGateway(simpleJobLauncher);

        return jobLaunchingGateway;
    }
    // end::gateway[]

    // tag::flow[]
    @Bean
    public IntegrationFlow integrationFlow(FileMessageToJobRequest fileMessageToJobRequest) {
        FileInboundChannelAdapterSpec source = Files
                .inboundAdapter(new File("target/myfiles"))
                .filter(new SimplePatternFileListFilter("*.txt"));
        return IntegrationFlows.from(source, this::configureEndpoint)
                .handle(fileMessageToJobRequest)
                .handle(jobLaunchingGateway())
                .log(LoggingHandler.Level.WARN, "headers.id + ': ' + payload") // response loggen
                .get();
    }

    private void configureEndpoint(SourcePollingChannelAdapterSpec channelAdapter) {
        PollerSpec poller = Pollers.fixedRate(1000).maxMessagesPerPoll(1);
        channelAdapter.poller(poller);
    }
    // end::flow[]
}
