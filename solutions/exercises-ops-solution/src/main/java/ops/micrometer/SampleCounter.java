package ops.micrometer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

// tag::code[]
@Component
public class SampleCounter implements InitializingBean {

    @Autowired
    private MeterRegistry registry;

    private Counter counter;

    @Override
    public void afterPropertiesSet() throws Exception {
        counter = Counter.builder("sample.tick.counter")
                .description("Zählt die Aufrufe der tick-Methode")
                .tags("app", "statistics")
                .register(registry);
    }

    public void count() {
        counter.increment();
    }
}
// end::code[]