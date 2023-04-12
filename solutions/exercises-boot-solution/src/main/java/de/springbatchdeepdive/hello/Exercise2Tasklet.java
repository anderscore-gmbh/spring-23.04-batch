package de.springbatchdeepdive.hello;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@JobScope
public class Exercise2Tasklet implements Tasklet {

    @Value("#{jobParameters['name']?:'World'}")
    private String name;

    @Value("#{jobParameters['count']?:1}")
    private int count;

    @Autowired
    private ApplicationContext context;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        System.out.printf("Hello %s!%n", name);
        count--;
        return count > 0 ? RepeatStatus.CONTINUABLE : RepeatStatus.FINISHED;
    }
}
