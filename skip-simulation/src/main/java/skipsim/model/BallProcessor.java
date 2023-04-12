package skipsim.model;

import java.util.Optional;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class BallProcessor implements ItemProcessor<Ball, Ball> {
	private static final Logger log = LoggerFactory.getLogger(BallProcessor.class);

    @Autowired
    private BallContainer ballContainer;

    private Semaphore semaphore = new Semaphore(0);

    @Override
    public Ball process(Ball ball) throws Exception {
        log.info("process: {}", ball);
        try {
            semaphore.acquire();
            ballContainer.setProcessing(Optional.of(ball));
            return ball;
        } finally {
            semaphore.acquire();
            ballContainer.setProcessing(Optional.empty());
        }
    }

    public void tick() {
        semaphore.release();
    }
}
