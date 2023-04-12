package skipsim;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import skipsim.fx.ResizableCanvas;
import skipsim.job.SkipSimulatorJobConfig;
import skipsim.model.BallContainer;
import skipsim.model.BallProcessor;

/**
 * Run with: --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml
 * PATH_TO_FX must point to the Java-FX lib directory.
 */
public class SkipSimulatorApp extends Application {
	private static final Logger log = LoggerFactory.getLogger(SkipSimulatorApp.class);
	
    @Autowired
    private BallContainer container;

    @Autowired
    private BallProcessor processor;

    @Autowired
    private JobLauncher launcher;

    @Autowired
    private Job skipSimulationJob;

    private ResizableCanvas canvas;
    private AnimationTimer timer;
    boolean running = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
    	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SkipSimulatorJobConfig.class);
        ctx.getAutowireCapableBeanFactory().autowireBean(this);

        timer = new AnimationTimer() {
            long next;

            @Override
            public void handle(long now) {
                if (now >= next) {
                    tick();
                    canvas.draw();
                    next = now + 1_000_000_000L;
                }
            }
        };
    }

    @Override
    public void start(Stage primaryStage) throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        canvas = new ResizableCanvas(container);

        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 1880, 400, Color.BLACK);
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty().subtract(100));

        Button startButton = new Button("Start");
        root.getChildren().add(startButton);
        startButton.setLayoutX(10);
        startButton.layoutYProperty().bind(canvas.heightProperty().add(20));
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            boolean started = false;

            @Override
            public void handle(ActionEvent event) {
                if (!running) {
                    runJob();
                    running = true;
                }
                if (started) {
                    timer.stop();
                    started = false;
                    startButton.setText("Start");
                } else {
                    timer.start();
                    started = true;
                    startButton.setText("Stop");
                }
            }
        });

        Button nextButton = new Button("Next");
        root.getChildren().add(nextButton);
        nextButton.setLayoutX(60);
        nextButton.layoutYProperty().bind(canvas.heightProperty().add(20));
        nextButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!running) {
                    runJob();
                    running = true;
                }
                tick();
            }
        });

        primaryStage.setTitle("Skip Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void tick() {
        canvas.draw();
        processor.tick();
    }

    private void runJob() {
        JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
        try {
            launcher.run(skipSimulationJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException ex) {
            log.error("Unable to start Job", ex);
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("Stop called, exiting...");
        System.exit(0);
    }
}
