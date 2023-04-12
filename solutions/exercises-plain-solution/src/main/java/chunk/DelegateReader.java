package chunk;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemReader;

// tag::code[]
public class DelegateReader implements ItemReader<BigDecimal> {

    private final ItemReader<? extends Number> delegate;

    public DelegateReader(ItemReader<? extends Number> delegate) {
        this.delegate = delegate;
    }

    @Override
    public BigDecimal read() throws Exception {
        Number value = delegate.read();
        return value == null ? null : BigDecimal.valueOf(value.longValue());
    }
}
// end::code[]