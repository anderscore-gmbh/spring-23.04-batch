package cloud.task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;

// tag::code[]
@SpringBootApplication
@EnableTask // <1>
public class CloudTaskHelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudTaskHelloWorldApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return (args) -> System.out.println("Hello, World!");
    }
}
// end::code[]