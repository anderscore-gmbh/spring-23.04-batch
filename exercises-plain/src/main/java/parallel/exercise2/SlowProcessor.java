package parallel.exercise2;

import java.util.concurrent.TimeUnit;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import chunk.SimpleProcessor;

@Component
public class SlowProcessor implements ItemProcessor<Integer, String> {
    private final ItemProcessor<? super Integer, ? extends String> delegate = new SimpleProcessor();

    @Override
    public String process(Integer item) throws Exception {
        String result = delegate.process(item);
        TimeUnit.MILLISECONDS.sleep(200);
        return result;
    }
}
