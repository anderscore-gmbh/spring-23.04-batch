package skipsim.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.item.support.ListItemWriter;

import skipsim.fx.Renderer;
import skipsim.fx.SimulationModel;
import skipsim.model.Ball.Mode;

public class BallContainer implements ItemReader<Ball>, ItemWriter<Ball>, SimulationModel {
	private static final Logger log = LoggerFactory.getLogger(BallContainer.class);

	enum SkipMode {
        NONE, READ, PROCESS, WRITE
    }

    private static final SkipMode skipMode = SkipMode.NONE;

    private final List<Ball> pool = new ArrayList<>();
    private Optional<Ball> processing = Optional.empty();
    private ItemReader<Ball> reader;
    private final ListItemWriter<Ball> writer = new ListItemWriter<>();
    private List<Ball> tx;

    public static BallContainer createOneInvalid() {
        BallContainer bc = new BallContainer();
        if (skipMode != SkipMode.NONE) {
            bc.pool.get(9).setInvalid(true);
            bc.pool.get(13).setInvalid(true);
            bc.pool.get(16).setInvalid(true);
        }
        return bc;
    }

    private BallContainer() {
        for (int no = 0; no < 20; no++) {
            Ball ball = new Ball();
            ball.setNo(no);
            pool.add(ball);
        }
    }

    @Override
    public synchronized Ball read() throws Exception {
        if (reader == null) {
            reader = new IteratorItemReader<>(pool);
        }
        Ball read = reader.read();
        log.info("read: {}", read);
        if (read != null) {
            if (read.isInvalid() && skipMode == SkipMode.READ) {
                throw new InvalidBallException();
            }
            tx.add(read);
            read.setMode(Mode.READ);
        }
        return read;
    }

    @Override
    public synchronized void write(List<? extends Ball> items) throws Exception {
        log.info("write: {}", items);
        if (skipMode == SkipMode.WRITE && items.stream().filter(Ball::isInvalid).findAny().isPresent()) {
            throw new InvalidBallException();
        }
        writer.write(items);
    }

    @Override
    public synchronized void render(Renderer renderer) {
        pool.forEach(renderer::renderToRead);
        processing.ifPresent(renderer::renderProcessing);
        writer.getWrittenItems().forEach(renderer::renderWritten);
    }

    public void begin() {
        log.info("begin transaction");
        tx = new ArrayList<>();
    }

    public void commit() {
        log.info("commit transaction");
        tx = null;
    }

    public void rollback() {
        log.info("rollback transaction");
        if (false) {
            tx.forEach(b -> b.setMode(Mode.NEW));
        }
        tx = null;
    }

    public synchronized void setProcessing(Optional<Ball> processing) throws InvalidBallException {
        this.processing = processing;
        if (processing.isPresent()
                && processing.get().isInvalid()
                && skipMode == SkipMode.PROCESS) {
            throw new InvalidBallException();
        }
    }
}
