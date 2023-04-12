package remote.duplicate;

import java.util.stream.IntStream;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class DuplicateReader implements ItemReader<Integer>, InitializingBean {
    @Value("#{jobParameters[count]?:100}")
    private int count = 100;
    private IteratorItemReader<Integer> delegate;

    @Override
    public void afterPropertiesSet() {
        delegate = new IteratorItemReader<>(IntStream.rangeClosed(1, count).boxed().iterator());
    }

    @Override
    public Integer read() {
        return delegate.read();
    }
}
