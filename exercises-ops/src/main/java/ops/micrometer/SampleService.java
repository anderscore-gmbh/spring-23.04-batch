package ops.micrometer;

import io.micrometer.core.annotation.Timed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SampleService {
    private static final Logger log = LogManager.getLogger(SampleService.class);

    // @formatter:off
    // tag::timed[]
    @Timed(value = "sample.sinus.timer",
            description = "Timer, der eine Sinuskurve produziert",
            extraTags = {"app", "statistics"})
    public void sinusDelay() throws InterruptedException {
        double x = System.nanoTime() * Math.PI / 60e9;
        long y = 250L - (long) (100 * Math.sin(x));
        log.debug("delaying {} ms", y);
        TimeUnit.MILLISECONDS.sleep(y);
    }
    // end::timed[]
    // @formatter:on
}
