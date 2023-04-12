package samples.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleCounter implements InitializingBean {

    @Autowired
    private MeterRegistry registry;

    private Counter counter;

    @Override
    public void afterPropertiesSet() throws Exception {
        counter = Counter.builder("sample.hello.counter")
                .description("Zählt die Hello-Aufrufe")
                .tags("app", "statistics")
                .register(registry);
    }

    public void count() {
        counter.increment();
    }
}
