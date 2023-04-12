package remote.partitioning;

import org.springframework.batch.core.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Job ausführen mit:
 * 
 * <pre>
 * org.springframework.batch.core.launch.support.CommandLineJobRunner 
 *    remote.partitioning.PartitionGaussManagerConfig -next partitionJob minValue(long)=1 maxValue(long)=100
 * </pre>
 */
@Configuration
public class PartitionGaussManagerConfig {

    @Bean
    Job remotePartitioningJob() {
        Job job = null;
        return job;
    }
}
