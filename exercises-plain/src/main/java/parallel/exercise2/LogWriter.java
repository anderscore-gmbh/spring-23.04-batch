package parallel.exercise2;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * A generic writer useful for debugging any jobs.
 */
@Component
public class LogWriter<T> implements ItemWriter<T> {
	private static final Logger log = LoggerFactory.getLogger(LogWriter.class);

    @Override
    public void write(List<? extends T> items) {
        String line = items.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        log.info(line);
    }

}
