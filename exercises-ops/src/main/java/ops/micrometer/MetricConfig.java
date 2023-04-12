package ops.micrometer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
@EnableScheduling
public class MetricConfig {

    @Autowired
    private SampleService sampleService;

    // tag::counter[]
    @Autowired
    private SampleCounter sampleCounter;

    @Scheduled(fixedRate = 1000L)
    void tick() throws InterruptedException {
        sampleCounter.count();
        sampleService.sinusDelay();
    }
    // end::counter[]

    // tag::timed[]
    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect();
    }
    // end::timed[]
}
