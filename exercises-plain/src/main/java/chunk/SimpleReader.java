package chunk;

import java.util.Iterator;
import java.util.stream.IntStream;

import org.springframework.batch.item.ItemReader;

//tag::code[]
public class SimpleReader implements ItemReader<Integer> {
    private final Iterator<Integer> iter;

    public SimpleReader(int numItems) {
        iter = IntStream.rangeClosed(1, numItems).boxed().iterator();
    }

    @Override
    public Integer read() {
        return iter.hasNext() ? iter.next() : null;
    }
}
//end::code[]