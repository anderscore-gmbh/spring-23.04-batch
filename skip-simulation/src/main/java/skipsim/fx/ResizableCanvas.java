package skipsim.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class ResizableCanvas extends Canvas {
	private static final Logger log = LoggerFactory.getLogger(ResizableCanvas.class);

	private final SimulationModel simulationModel;

    public ResizableCanvas(SimulationModel simulationModel) {
        this.simulationModel = simulationModel;

        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    public void draw() {
        log.debug("draw");
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        Renderer renderer = new Renderer(width, height, gc);
        simulationModel.render(renderer);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    public void update() {
        log.debug("update");
        Platform.runLater(this::draw);
    }
}
