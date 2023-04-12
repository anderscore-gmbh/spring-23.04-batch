package sample.micrometer;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

@SpringJUnitConfig
public class TimedAspectTest {

    @TestConfiguration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class Config {

        @Bean
        SimpleMeterRegistry registry() {
            SimpleMeterRegistry registry = new SimpleMeterRegistry();
            new ClassLoaderMetrics().bindTo(registry);
            Metrics.addRegistry(registry);
            return registry;
        }

        @Bean
        TimedAspect timedAspect() {
            return new TimedAspect(registry());
        }

        @Bean
        TimedService timedService() {
            return new TimedService();
        }
    }

    static class TimedService {
        @Timed("test.go")
        public void go() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
        }
    }

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private TimedService service;


    @Test
    void test() throws InterruptedException {
        service.go();

        for (Meter meter : registry.getMeters()) {
            System.out.println(meter.getId() + ": " + meter);
        }

        Timer timer = registry.get("test.go").timer();
        System.out.printf("%d times: %1.2f/%1.2f s%n",
                timer.count(),
                timer.mean(TimeUnit.SECONDS),
                timer.max(TimeUnit.SECONDS));
    }
}
