package chunk.exercise;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@StepScope
public class FailedFractionReader implements ItemReader<Fraction>, StepExecutionListener {
    public static final ExitStatus WARNING = new ExitStatus("WARNING", "Divison durch Null");

    @Value("#{jobExecutionContext[T(chunk.exercise.FractionProcessor).FAILED_FRACTIONS]}")
    private List<Fraction> failedFractions;

    private IteratorItemReader<Fraction> delegate;

    @Override
    public Fraction read() {
        return delegate.read();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        delegate = new IteratorItemReader<Fraction>(failedFractions);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return failedFractions.isEmpty() ? null : WARNING;
    }
}
