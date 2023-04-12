package parallel.exercise1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Zerlegt die Zahlenreihe 1..100 in grid-size große Teile und legt diese als minValue und maxValue im jeweiligen
 * ExecutionContext ab
 */
@Component
@StepScope
public class NumberRangePartitioner implements Partitioner {
    @Value("#{stepExecutionContext[minValue]?:1}")
    private int minValue = 1;
    @Value("#{stepExecutionContext[maxValue]?:100}")
    private int maxValue = 100;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int targetSize = (maxValue - minValue) / gridSize + 1;

        Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
        int number = 0;
        int start = minValue;
        int end = start + targetSize - 1;

        while (start <= maxValue) {
            ExecutionContext executionContext = new ExecutionContext();
            result.put("partition" + number, executionContext);

            if (end >= maxValue) {
                end = maxValue;
            }
            executionContext.putInt("minValue", start);
            executionContext.putInt("maxValue", end);
            start += targetSize;
            end += targetSize;
            number++;
        }

        return result;
    }

}
