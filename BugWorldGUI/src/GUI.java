import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUI extends Application {
	
	private double speed = 16; // slider to control this double???
	World theWorld;
	private BorderPane mainBorderPane; // holds the main BorderPane for easy access
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		// create new borderPane for UI
		BorderPane mainBP = new BorderPane();
		mainBP.setPrefSize(1000, 550); // set preferred size of UI window: width, height.
		mainBP.setTop(this.addBorder()); // add a HBox border at top of screen
		mainBP.setBottom(this.addBorder()); // add a HBox border at top of screen
		mainBP.setCenter(this.addMapPane()); // add ?? with map for map view
		mainBP.setLeft(this.addButtonPane()); // add VBox with buttons as left pane
		mainBP.setRight(this.rightBorder());
		this.mainBorderPane = mainBP; // update mainBorderPane
		// create new scene using mainBorderPane and pref width and height from mainBorderPane
		final Scene scene = new Scene(mainBorderPane, mainBorderPane.getPrefWidth(), mainBorderPane.getPrefHeight());
		
		primaryStage.setTitle("BUG WORLD");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public HBox addBorder() {
		/*
		 * Returns an HBox with nothing in it, pref width 1000, height 25
		 * Used for making border in UI using top and bottom BorderPane nodes
		 */
		HBox border = new HBox();
		border.setPrefSize(1000, 25);
		border.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
		return border;
	}
	
	public VBox addButtonPane() {
		/*
		 * Buttons panel in UI, left node in mainBorderPane
		 */
		VBox buttons = new VBox(); // create new VBox
		buttons.setPadding(new Insets(25, 25, 0, 25));
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.TOP_CENTER);
		buttons.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
		buttons.setPrefSize(245, 500);
		
		// I don't really know how to work this CSS stuff 
		// http://www.java2s.com/Tutorials/Java/JavaFX_How_to/VBox/Add_border_style_to_VBox.htm
		// Used this to help me make a border around VBox buttons
		String cssBorder = "-fx-border-color: grey;\n" +
                "-fx-border-insets: 0;\n" +
                "-fx-border-width: 0 5 0 5;\n" +
                "-fx-border-style: solid;\n";  
		buttons.setStyle(cssBorder);
		
		
		//Create label for speedSlider
		Label sliderLabel = new Label("Select the speed: ");
		buttons.getChildren().add(sliderLabel);
		
		//Create slider for speed
		Slider speedSlider = new Slider(10, 40, 25);
		speedSlider.setShowTickMarks(true); //enable tick marks
		speedSlider.setShowTickLabels(true); //enable labels on slider
		speedSlider.setMajorTickUnit(10); //display ticks of 10
		speedSlider.setMinorTickCount(5); //add minor tick count of 5
		speedSlider.setBlockIncrement(10); // set value of block increment
		speedSlider.setPrefWidth(150);
		// Add listener for slider
		speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> 
						observable , Number oldValue, Number newValue) {
				//update double speed with newValue
				speed = (double) newValue;
				
				
			}
		});
		buttons.getChildren().add(speedSlider); // add slider to VBox

		//add play button
		Button playBTN = new Button();
		playBTN.setText("PLAY");
		playBTN.setPrefWidth(150);
		buttons.getChildren().add(playBTN); // add button to VBox
		
		//add pause button
		Button pauseBTN = new Button();
		pauseBTN.setText("PAUSE");
		pauseBTN.setPrefWidth(150);
		buttons.getChildren().add(pauseBTN); // add button to VBox
		
		//add stop button
		Button stopBTN = new Button();
		stopBTN.setText("STOP");
		stopBTN.setPrefWidth(150);
		buttons.getChildren().add(stopBTN); // add button to VBox
		
		/*
		 * STILL NEED TO ADD EVENT HANDLERS
		 * 
		 * maybe add:
		 * 		add item
		 * 		remove item
		 * 		save
		 * 		load
		 * 		quit
		 */
		
		return buttons;
	}
	
	public Group addMapPane() { // atm just draws a green rectangle as backdrop for animation
		// create new world with double width, double height
		this.theWorld = new World(750, 500); 
		Group map = new Group();
//		Rectangle BG = new Rectangle();
//		BG.setX(250);
//		BG.setY(25);
//		BG.setWidth(750);
//		BG.setHeight(500);
//		BG.setFill(Color.GREEN);
//		map.getChildren().add(BG);
		
		for (Bug b : theWorld.getBugList()) {
			ImageView bugImageView = b.getBugImage();
			bugImageView.setX(b.getX());
			bugImageView.setY(b.getY());
			map.getChildren().add(bugImageView);
		}
		
		for (Plant p : theWorld.getPlantList()) {
			ImageView plantImageView = p.getPlantImage();
			plantImageView.setPreserveRatio(true);
			plantImageView.setX(p.getX());
			plantImageView.setY(p.getY());
			plantImageView.setFitWidth(p.getSize()*0.9);
			plantImageView.setFitHeight(p.getSize()*0.9);
			// maybe add scale depending on size of plant
			map.getChildren().add(plantImageView);
		}
		
		// create KeyFrame for animating theWorld
		KeyFrame frame = new KeyFrame(Duration.millis(speed), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
				removeTheDead(map);
				
				for (Bug b : theWorld.getBugList()) {
					// getDestination in Bug requires Map bugList, Map plantList, world width and height.
					b.getDestination(theWorld.getBugList(), theWorld.getPlantList(), theWorld.getWidth(), theWorld.getHeight());
					//once destination decided move image
					ImageView bugImageView = b.getBugImage();
					bugImageView.setX(b.getX());
					bugImageView.setY(b.getY());
				}
				
				for (Plant p : theWorld.getPlantList()) {
					p.growPlant();
					ImageView plantImageView = p.getPlantImage();
					plantImageView.setFitWidth(p.getSize()*0.9);
					plantImageView.setFitHeight(p.getSize()*0.9);

				}
			}
		});
		
		Timeline timeline = new Timeline();
		timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
		timeline.getKeyFrames().add(frame);
		timeline.play();
		
		return map;
	}
	
	public VBox rightBorder() {
		VBox border = new VBox();
		border.setPrefSize(5, 500);
		border.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
		return border;
	}
	
	public void removeTheDead(Group map) {
		List<Bug> newBugList = new ArrayList<Bug>();
		for (Bug b : theWorld.getBugList()) {
			if (b.getHunger() > 0) {
				newBugList.add(b);
			}
			else {
				map.getChildren().remove(b.getBugImage());
			}
		}
		theWorld.setBugList(newBugList);
		
		List<Plant> newPlantList = new ArrayList<Plant>();
		for (Plant p : theWorld.getPlantList()) {
			if (p.getSize() > 1) {
				newPlantList.add(p);
			}
			else {
				map.getChildren().remove(p.getPlantImage());
			}
		}
		theWorld.setPlantList(newPlantList);
	}
	
	
	public static void main(String[] args) {
		launch();

	}
}
