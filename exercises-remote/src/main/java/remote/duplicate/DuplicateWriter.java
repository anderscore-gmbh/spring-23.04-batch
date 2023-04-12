package remote.duplicate;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * A generic writer useful for debugging any jobs.
 */
@Component
public class DuplicateWriter implements ItemWriter<Integer> {

    @Override
    public void write(List<? extends Integer> items) {
        String line = items.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        System.out.println(line);
    }

}
