package chunk.exercise;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * A generic writer useful for debugging any jobs.
 */
@Component
public class SystemOutWriter implements ItemWriter<Object> {

    @Override
    public void write(List<? extends Object> items) {
        String line = items.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        System.out.println(line);
    }

}
