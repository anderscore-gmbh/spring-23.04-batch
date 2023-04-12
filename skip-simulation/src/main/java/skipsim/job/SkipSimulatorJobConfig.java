package skipsim.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import skipsim.model.Ball;
import skipsim.model.BallContainer;
import skipsim.model.BallProcessor;
import skipsim.model.InvalidBallException;

@Configuration
@EnableBatchProcessing
public class SkipSimulatorJobConfig extends DefaultBatchConfigurer {
	private static final Logger log = LoggerFactory.getLogger(SkipSimulatorJobConfig.class);

	@Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    BallContainer ballContainer() {
        return BallContainer.createOneInvalid();
    }

    @Bean
    BallProcessor ballProcessor() {
        return new BallProcessor();
    }

    // tag::code[]
    @Bean
    TaskletStep skipSimulationStep() {
        return stepBuilderFactory.get("skip-simulation-step")
                .listener(logListener())
                .<Ball, Ball>chunk(6)
                .reader(ballContainer())
                .processor(ballProcessor())
                .writer(ballContainer())
                .faultTolerant().skip(InvalidBallException.class).skipLimit(3)
                .build();
    }
    // end::code[]

    @Bean
    StepExecutionListener logListener() {
        return new StepExecutionListener() {

            @Override
            public void beforeStep(StepExecution stepExecution) {
                log.info("Starting Step " + stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                log.info("Completed Step " + stepExecution);
                return stepExecution.getExitStatus();
            }
        };
    }

    @Bean
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExectutor = new ThreadPoolTaskExecutor();
        return taskExectutor;
    }

    @Bean
    Job skipSimulationJob() {
        return jobBuilderFactory.get("skip-simulation-job").start(skipSimulationStep()).build();
    }

    @Override
    protected JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = (SimpleJobLauncher) super.createJobLauncher();
        jobLauncher.setTaskExecutor(taskExecutor());
        return jobLauncher;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        ResourcelessTransactionManager txManager = new ResourcelessTransactionManager();
        BallContainer ballContainer = ballContainer();
        ProxyFactory proxyFactory = new ProxyFactory(txManager);
        proxyFactory.addAdvisor(new RegexpMethodPointcutAdvisor(".*\\.getTransaction",
                (MethodBeforeAdvice) (method, args, target) -> ballContainer.begin()));
        proxyFactory.addAdvisor(new RegexpMethodPointcutAdvisor(".*\\.commit",
                (MethodBeforeAdvice) (method, args, target) -> ballContainer.commit()));
        proxyFactory.addAdvisor(new RegexpMethodPointcutAdvisor(".*\\.rollback",
                (MethodBeforeAdvice) (method, args, target) -> ballContainer.rollback()));
        Object wrapped = proxyFactory.getProxy();
        return (PlatformTransactionManager) wrapped;
    }
}
