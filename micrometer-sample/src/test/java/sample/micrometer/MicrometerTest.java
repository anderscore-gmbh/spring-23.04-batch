package sample.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.logging.Log4j2Metrics;
import io.micrometer.core.instrument.search.RequiredSearch;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

public class MicrometerTest {

    private MeterRegistry registry = new SimpleMeterRegistry();

    @Test
    void test() {
        Counter counter = Counter.builder("sample.hello.counter")
                .description("Zählt die Aufrufe")
                .tags("app", "statistics")
                .register(registry);

        counter.increment();

        for (Meter meter : registry.getMeters()) {
            System.out.println(meter.getId() + ": " + meter);
        }

        RequiredSearch search = registry.get("sample.hello.counter");
        System.out.println("count: " + search.counter().count());
    }

    @Test
    void testGlobalRegistry() {
        Counter counter = Counter.builder("sample.hello.counter")
                .description("Zählt die Aufrufe")
                .tags("app", "statistics")
                .register(registry);

        Metrics.addRegistry(registry);
        counter.increment();
        Metrics.counter("sample.hello.counter").increment();

        System.out.println("count: " + Metrics.counter("sample.hello.counter").count());
    }

    @Test
    void testBuiltinMetrics() {
        new JvmGcMetrics().bindTo(registry);
        new Log4j2Metrics().bindTo(registry);
        for (Meter meter : registry.getMeters()) {
            System.out.println(meter.getId() + ": " + meter);
        }
    }
}
