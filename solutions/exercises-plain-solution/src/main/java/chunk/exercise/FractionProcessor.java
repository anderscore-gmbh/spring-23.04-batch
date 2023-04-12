package chunk.exercise;

import java.util.LinkedList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FractionProcessor implements ItemProcessor<Fraction, String> {
    public static final String FAILED_FRACTIONS = "failedFractions";
    private List<Fraction> failedFractions = new LinkedList<>();

    @Override
    public String process(Fraction fraction) {
        return fraction.toDescription();
    }

    @OnSkipInProcess
    void onSkipInProcess(Fraction fraction, Throwable th) {
        failedFractions.add(fraction);
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        stepExecution.getExecutionContext().put(FAILED_FRACTIONS, failedFractions);
    }
}
