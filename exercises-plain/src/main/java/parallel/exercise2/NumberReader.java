package parallel.exercise2;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import chunk.SimpleReader;

@Component
@StepScope
public class NumberReader implements ItemReader<Integer>, InitializingBean {
    @Value("#{jobParameters[count]?:100}")
    private int count;

    private SimpleReader delegate;

    @Override
    public void afterPropertiesSet() {
        delegate = new SimpleReader(count);
    }

    @Override
    public Integer read() {
        return delegate.read();
    }
}
