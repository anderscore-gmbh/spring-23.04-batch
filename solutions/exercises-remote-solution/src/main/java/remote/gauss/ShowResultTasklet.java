package remote.gauss;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ShowResultTasklet implements Tasklet {

    /**
     * Ergebnis ausgeben...
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        int sum = (Integer) chunkContext.getStepContext().getJobExecutionContext().get("sum");
        System.out.println("Result: " + sum);
        return RepeatStatus.FINISHED;
    }

}
