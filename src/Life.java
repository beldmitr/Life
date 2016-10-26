import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Life extends Application {

	private final double WIDTH = 1000;
	private final double HEIGHT = 700;

	private final int SIZEX = 20;
	private final int SIZEY = 20;

	private int cols;
	private int rows;

	private Canvas canvas;
	private MenuItem mnStop;

	boolean[][] field;
	private MenuItem mnNext;
	private MenuItem mnPlay;
	private MenuItem mnExit;
	private MenuItem mnNew;
	private Timeline timeline;
	private CustomMenuItem mnSlider;
	private Slider sldTimer;
	private MenuItem mnNewRandom;
	private Scene scene;
	private static boolean isStarted;

	Stage primaryStage;
	private VBox root;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;

		root = new VBox();
		// 25 is height of the main menu
		scene = new Scene(root, WIDTH, HEIGHT + 25);

		initLife();
		createComponents(root);
		addListeners(scene);
		drawGrid();

		primaryStage.setTitle("Life");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initLife() {

		cols = (int) (WIDTH / SIZEX);
		rows = (int) (HEIGHT / SIZEY);

		isStarted = false;

		field = new boolean[rows][cols];
	}

	private void setRandomField() {
		Random rand = new Random();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				field[i][j] = rand.nextBoolean();
			}
		}
	}

	private void createComponents(VBox root) {

		sldTimer = new Slider(100, 1000, 100);
		

		MenuBar menu = new MenuBar();

		Menu mnFile = new Menu("File");
		Menu mnGame = new Menu("Game");

		menu.getMenus().addAll(mnFile, mnGame);

		mnNewRandom = new MenuItem("New random");
		mnNew = new MenuItem("New");
		mnExit = new MenuItem("Exit");

		mnNewRandom.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		mnNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		mnExit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));

		mnSlider = new CustomMenuItem(sldTimer);
		mnSlider.setHideOnClick(false);

		mnPlay = new MenuItem("Play");
		mnNext = new MenuItem("Next");
		mnStop = new MenuItem("Stop");

		mnStop.setDisable(true);

		mnPlay.setAccelerator(KeyCombination.keyCombination("P"));
		mnNext.setAccelerator(KeyCombination.keyCombination("SPACE"));
		mnStop.setAccelerator(KeyCombination.keyCombination("S"));

		mnFile.getItems().addAll(mnNew, mnNewRandom, mnExit);
		mnGame.getItems().addAll(mnSlider, new SeparatorMenuItem(), mnPlay, mnNext, mnStop);

		root.getChildren().add(menu);

		initCanvas();

		root.getChildren().add(canvas);

		timeline = new Timeline(new KeyFrame(Duration.millis(sldTimer.getValue()), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				step();
			}
		}));

		timeline.setCycleCount(Timeline.INDEFINITE);

	}

	private void initCanvas() {
		canvas = new Canvas(WIDTH, HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, WIDTH, HEIGHT);
	}

	private void drawGrid() {
		GraphicsContext gc = canvas.getGraphicsContext2D();

		gc.setLineWidth(1);

		for (int i = 0; i <= rows; i++) {
			for (int j = 0; j <= cols; j++) {
				gc.strokeLine(SIZEX * j, 0, SIZEX * j, HEIGHT);
			}

			gc.strokeLine(0, SIZEY * i, WIDTH, SIZEY * i);
		}

	}

	private void addListeners(Scene scene) {

		

		mnExit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		mnNew.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				timeline.stop();
				initLife();
				drawGrid();
				drawField();
				mnPlay.setDisable(false);
				mnStop.setDisable(true);
			}
		});

		mnNewRandom.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				timeline.stop();
				initLife();
				setRandomField();
				drawGrid();
				drawField();
				mnPlay.setDisable(false);
				mnStop.setDisable(true);
			}
		});

		sldTimer.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				timeline.stop();

				KeyFrame kframe = new KeyFrame(Duration.millis(sldTimer.getValue()), new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						step();
					}
				});

				timeline.getKeyFrames().setAll(kframe);

				if (isStarted) {
					timeline.play();
				}
			}
		});

		mnPlay.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				isStarted = true;
				timeline.play();
				mnPlay.setDisable(true);
				mnStop.setDisable(false);
			}
		});

		mnStop.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				isStarted = false;
				timeline.stop();
				mnPlay.setDisable(false);
				mnStop.setDisable(true);
			}
		});

		mnNext.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				step();
			}
		});

		canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			GraphicsContext gc = canvas.getGraphicsContext2D();

			@Override
			public void handle(MouseEvent event) {

				int i = (int) (event.getX() / SIZEX);
				int j = (int) (event.getY() / SIZEY);

				if (event.getButton() == MouseButton.PRIMARY) {
					gc.setFill(Color.GREEN);
					field[j][i] = true;
				}

				if (event.getButton() == MouseButton.SECONDARY) {
					gc.setFill(Color.WHITE);
					field[j][i] = false;
				}

				drawField();
			}
		});

	}

	private void step() {

		boolean[][] temp = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int neighbors = 0;
				boolean topBorder = false;
				boolean rightBorder = false;
				boolean bottomBorder = false;
				boolean leftBorder = false;

				if (i == 0) {
					topBorder = true;
				}

				if (j == 0) {
					leftBorder = true;
				}

				if (i == rows - 1) {
					bottomBorder = true;
				}

				if (j == cols - 1) {
					rightBorder = true;
				}

				if (!topBorder && !leftBorder && field[i - 1][j - 1]) {
					neighbors++;
				}
				if (!topBorder && field[i - 1][j]) {
					neighbors++;
				}
				if (!topBorder && !rightBorder && field[i - 1][j + 1]) {
					neighbors++;
				}
				if (!rightBorder && field[i][j + 1]) {
					neighbors++;
				}
				if (!bottomBorder && !rightBorder && field[i + 1][j + 1]) {
					neighbors++;
				}
				if (!bottomBorder && field[i + 1][j]) {
					neighbors++;
				}
				if (!bottomBorder && !leftBorder && field[i + 1][j - 1]) {
					neighbors++;
				}
				if (!leftBorder && field[i][j - 1]) {
					neighbors++;
				}

				if (field[i][j]) {
					if (neighbors == 2 || neighbors == 3) {
						temp[i][j] = true;
					} else {
						temp[i][j] = false;
					}

				} else {
					if (neighbors == 3) {
						temp[i][j] = true;
					} else {
						temp[i][j] = false;
					}
				}
			}
		}

		field = temp;
		drawField();
	}

	private void drawField() {
		GraphicsContext gc = canvas.getGraphicsContext2D();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				if (field[i][j]) {
					gc.setFill(Color.GREEN);
				} else {
					gc.setFill(Color.WHITE);
				}

				gc.fillRect(j * SIZEX + 1, i * SIZEY + 1, WIDTH / cols - 2, HEIGHT / rows - 2);

			}
		}
	}

}
