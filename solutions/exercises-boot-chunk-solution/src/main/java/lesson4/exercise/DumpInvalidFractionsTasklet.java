package lesson4.exercise;

import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class DumpInvalidFractionsTasklet implements Tasklet {
    public static final ExitStatus WARNING = new ExitStatus("WARNING", "Divison by zero");

	@Value("#{jobExecutionContext[invalidFractions]}")
	private List<Fraction> invalidFractions;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		System.out.println("\n:Skipped fractions:");
		invalidFractions.forEach(System.out::println);
		return RepeatStatus.FINISHED;
	}

	@AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        return invalidFractions.isEmpty() ? null : WARNING;
    }
}
