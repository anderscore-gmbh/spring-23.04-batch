package remote.duplicate;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class DuplicateProcessor implements ItemProcessor<Integer, Integer> {

    @Override
    public Integer process(Integer item) throws Exception {
        return Integer.valueOf(2 * item.intValue());
    }
}
