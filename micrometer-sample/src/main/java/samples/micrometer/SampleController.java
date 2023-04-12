package samples.micrometer;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @Autowired
    private SampleCounter counter;

    @GetMapping("/hello")
    @Timed("sample.hello.time")
    public String hello(String name) {
        counter.count();
        return "Hello " + name + "!";
    }
}
