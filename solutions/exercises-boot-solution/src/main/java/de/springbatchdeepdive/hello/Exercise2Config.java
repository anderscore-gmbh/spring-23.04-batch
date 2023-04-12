package de.springbatchdeepdive.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;

import java.lang.invoke.MethodHandles;

@Configuration
public class Exercise2Config {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Exercise2Tasklet exercise2Tasklet;

    @Bean
    Step exercise2Step() {
        return stepBuilderFactory.get("exercise2Step")
                .tasklet(exercise2Tasklet)
                .build();
    }

    @Bean
    Job exercise2Job() {
        return jobBuilderFactory.get("exercise2Job")
                .start(exercise2Step())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    /**
     * Der {@link ExitCodeMapper} ist eine Spring-Batch Klasse, die von Spring-Boot standardmäßig
     * nicht verwendet wird, siehe {@link de.springbatchdeepdive.hello.ExitCodeJobExecutionEventListener}.
     */
    @Bean
    ExitCodeMapper exitCodeMapper() {
        SimpleJvmExitCodeMapper mapper = new SimpleJvmExitCodeMapper();
        mapper.getMapping().put(ExitStatus.COMPLETED.getExitCode(), 17);
        return mapper;
    }

    /**
     * Der {@link ExitCodeExceptionMapper} ist eine Spring-Boot Klasse, mit welcher
     * man für Exceptions einen Exit-Code festlegen kann.
     */
    @Bean
    ExitCodeExceptionMapper exitCodeExceptionMapper() {
        return new ExitCodeExceptionMapper() {
            @Override
            public int getExitCode(Throwable exception) {
                log.info("mapping: " + exception);
                if (NestedExceptionUtils.getRootCause(exception) instanceof JobInstanceAlreadyCompleteException) {
                    return -17;
                }
                return 1;
            }
        };
    }
}
