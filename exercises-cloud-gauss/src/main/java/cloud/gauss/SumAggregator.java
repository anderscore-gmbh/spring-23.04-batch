package cloud.gauss;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.partition.support.DefaultStepExecutionAggregator;
import org.springframework.stereotype.Component;

@Component
public class SumAggregator extends DefaultStepExecutionAggregator {
    private static final Logger log = LoggerFactory.getLogger(SumAggregator.class);

    @Override
    public void aggregate(StepExecution result, Collection<StepExecution> executions) {
        super.aggregate(result, executions);
        if (executions == null) {
            return;
        }
        int total = 0;
        for (StepExecution stepExecution : executions) {
            int sum = stepExecution.getExecutionContext().getInt("sum");
            total += sum;
        }
        result.getExecutionContext().putInt("sum", total);
        log.info("total: {}", total);
    }
}
