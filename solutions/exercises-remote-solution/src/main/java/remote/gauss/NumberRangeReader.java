package remote.gauss;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class NumberRangeReader extends AbstractItemCountingItemStreamItemReader<Integer> {
    @Value("#{stepExecutionContext[minValue]}") // Late Binding wg. StepScope
    private int minValue;
    @Value("#{stepExecutionContext[maxValue]}")
    private int maxValue;

    private int current;

    public NumberRangeReader() {
        setName("numberRangeReader"); // Name ist notwendig, um Fortschritt im ExecutionContext zu speichern.
    }

    @Override
    protected Integer doRead() {
        if (current <= maxValue) {
            return current++;
        }
        return null;
    }

    @Override
    protected void doOpen() {
        current = minValue;
    }

    @Override
    protected void doClose() {
    }
};
