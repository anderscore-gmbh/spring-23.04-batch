package parallel.exercise2;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

public class ProducerConsumerQueue<T> extends StepExecutionListenerSupport
	implements ItemWriter<T>, ItemReader<T> {
    private static final Logger log = LoggerFactory.getLogger(ProducerConsumerQueue.class);

    private final BlockingQueue<T> queue;
	private final T endMarker;
	
	public ProducerConsumerQueue(BlockingQueue<T> queue, T endMarker) {
		this.queue = queue;
		this.endMarker = endMarker;
	}

	public ProducerConsumerQueue(int queueCapacity, T endMarker) {
		this(new ArrayBlockingQueue<>(queueCapacity), endMarker);
	}

	@Override
	public void write(List<? extends T> items) {
		try {
			for (T item : items) {
				queue.put(item);
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public T read() {
		try {
			T value = queue.take();
			if (Objects.equals(value, endMarker)) {
				log.debug("End-marker read.");
				putendMarker();
				return null;
			}
			return value;
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return endMarker;
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.debug("Step finished.");
		putendMarker();
		return super.afterStep(stepExecution);
	}

	private void putendMarker() {
		write(Collections.singletonList(endMarker));
	}
}
