package remote.gauss;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class SumItemWriter implements ItemWriter<Integer> {
    private static final Logger log = LoggerFactory.getLogger(SumItemWriter.class);
    private ExecutionContext executionContext;

    @BeforeStep
    void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
    }

    @Override
    public void write(List<? extends Integer> items) throws Exception {
        log.debug("write: {}", items);
        int sum = executionContext.getInt("sum", 0);
        for (Integer item : items) {
            sum += item;
        }
        executionContext.putInt("sum", sum);
    }
}
