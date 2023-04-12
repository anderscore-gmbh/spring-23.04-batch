package de.springbatchdeepdive.hello;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.batch.JobExecutionEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Dieser Listener sorgt dafür, dass ein {@link ExitCodeMapper} innerhalb
 * einer Spring-Boot Anwendung verwendet werden kann. Dafür muss allerdings
 * die main-Methode mit {@link System#exit} verlassen werden:
 * <pre>{@code
 *    public static void main(String[] args) {
 * 		System.exit(
 *        SpringApplication.exit(
 * 		    SpringApplication.run(HelloBootApplication.class, args)));
 *    }
 * }</pre>
 */
// tag::code[]
@Component
public class ExitCodeJobExecutionEventListener
        implements ApplicationListener<JobExecutionEvent>, ExitCodeGenerator, InitializingBean {

    @Autowired(required = false)
    private ExitCodeMapper exitCodeMapper;
    private int exitCode;

    @Override
    public void afterPropertiesSet() {
        if (exitCodeMapper == null) {
            exitCodeMapper = new SimpleJvmExitCodeMapper();
        }
    }

    @Override
    public void onApplicationEvent(JobExecutionEvent event) { // alternativ: @EventListener
        ExitStatus exitStatus = event.getJobExecution().getExitStatus();
        exitCode = exitCodeMapper.intValue(exitStatus.getExitCode());
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
// end::code[]