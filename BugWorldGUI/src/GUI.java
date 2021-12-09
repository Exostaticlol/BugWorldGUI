import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUI extends Application {
	
	private double speed = 1; // Controlled by speedSlider
	private World theWorld; // Hold reference to the world
	private String playPause = "Pause"; // Used to track if animation is playing or paused
	private BorderPane mainBorder; // Easy access to mainBorderPane
	private int addNumber = 10; // controlled by addSlider
	private String addElement = "Butterfly"; // controlled by combo box
	private double bgmVolume = 5; // Controlled by BGSlider
	private double pressVolume = 5; // controlled by pressSlider
	private MediaPlayer melody; // Reference to BG music
	private AudioClip BTNpress; // reference to BTN press sound
	
	private int counter = 0; // when int counter hits 200 add a new flower and shrub
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		/* Create new BorderPane and use addMethods to setup Panes in borderPane. */
		BorderPane mainBP = new BorderPane();
		mainBorder = mainBP;
		mainBP.setPrefSize(1000, 600); // set preferred size of UI window: width, height.
		
		/* Create new timeline here as addMapPane and AddButtonPane both require access to timeline
		 * Timeline is setup and controlled in addMap method. */
		Timeline timeline = new Timeline(); 
		
		/* Create all Nodes for BorderPane to hold */
		mainBP.setTop(this.addBorder()); // add a HBox border at top of screen
		mainBP.setBottom(this.addBorder()); // add a HBox border at top of screen
		mainBP.setCenter(this.addMapPane(timeline)); // add ?? with map for map view
		mainBP.setLeft(this.addButtonPane(timeline)); // add VBox with buttons as left pane
		mainBP.setRight(this.rightBorder());
		
		BTNpress = new AudioClip(getClass().getResource("/SFX/171697__nenadsimic__menu-selection-click.wav").toString());
		
		/* Initialize mp3 melody loop */
		Media loop = new Media(getClass().getResource("/SFX/Melody.wav").toString());
		MediaPlayer melodyLoop = new MediaPlayer(loop);
		melody = melodyLoop;
		melodyLoop.setCycleCount(MediaPlayer.INDEFINITE);
		melodyLoop.setVolume(bgmVolume);
		melodyLoop.play();
		
		// quick method to auto spawn life into world.
		this.setWorldLife((Group) mainBP.getCenter());
		
		/* Create new scene using mainBP and pref width and height from mainBP */
		final Scene scene = new Scene(mainBP, mainBP.getPrefWidth(), mainBP.getPrefHeight());
		
		/* Set up Stage then show. */
		primaryStage.setTitle("BUG WORLD");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private HBox addBorder() {
		/* Returns an HBox with nothing in it, pref width 1000, height 25
		 * Used for making border in UI using top and bottom BorderPane nodes */
		HBox border = new HBox();
		border.setPrefSize(1000, 25);
		border.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
		return border;
	}
	
	private VBox addButtonPane(Timeline timeline) {
		/* Creates a buttons panel in UI on the left node in mainBorderPane.
		 * Method requires Timeline for play, pause, stop and animation speed control. */
		VBox buttons = new VBox(); // create new VBox
		buttons.setPadding(new Insets(25, 25, 0, 25));
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.TOP_CENTER);
		buttons.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
		buttons.setPrefSize(245, 550);
		
		/* I don't really know how to work this CSS stuff 
		 * http://www.java2s.com/Tutorials/Java/JavaFX_How_to/VBox/Add_border_style_to_VBox.htm
		 * Used this to help me make a border around VBox buttons */
		String cssBorder = "-fx-border-color: grey;\n" +
                "-fx-border-insets: 0;\n" +
                "-fx-border-width: 0 5 0 5;\n" +
                "-fx-border-style: solid;\n";  
		buttons.setStyle(cssBorder);
		
		
		/* Create label for speedSlider */
		Label sliderLabel = new Label("Select the speed: ");
		buttons.getChildren().add(sliderLabel);
		
		/* Create slider for animation speed control. */
		// minimum value set to 0.2, at 0 animation would pause and buttons would not be as useful. Value of 2 runs at 200% speed.
		Slider speedSlider = new Slider(0.2, 2, 1); 
		speedSlider.setShowTickMarks(true); //enable tick marks
		speedSlider.setShowTickLabels(true); //enable labels on slider
		speedSlider.setMajorTickUnit(0.2); //display ticks of 10
		speedSlider.setMinorTickCount(1); //add minor tick count of 5
		speedSlider.setBlockIncrement(0.1); // set value of block increment
		speedSlider.setPrefWidth(150);
		// Add listener for slider
		speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> 
						observable , Number oldValue, Number newValue) {
				//update double speed with newValue
				speed = (double) newValue;
				timeline.setRate(speed);
				
			}
		});
		buttons.getChildren().add(speedSlider); // add slider to VBox

		//add play/pause button
		Button playPuaseBTN = new Button();
		/* animation starts playing so playPauseBTN will start on pause by default. 
		 * and will update to play when pushed */
		playPuaseBTN.setText("PLAY"); 
		playPuaseBTN.setPrefWidth(150);
		buttons.getChildren().add(playPuaseBTN); // add button to VBox
		//set action for playBTN
		playPuaseBTN.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				BTNpress.play(pressVolume); // BTNpress sound
				/* if String playPause = "Play" animation is currently playing
				 * Will need to pause animation and change playPause to "Pause", then change button text to match. */
				if (playPause.equals("Play")) { 
					VBox thisVBox = (VBox) mainBorder.getLeft(); // get leftVBox from mainBorder.
					Button playPauseBTN = (Button) thisVBox.getChildren().get(2); // playPause button is always at index 2 currently
					playPauseBTN.setText("PLAY"); // update button text to reflect new String
					playPause = "Pause";
					timeline.pause();
					//edit Text info, info changes if animation is stopped.
					Text info = (Text) thisVBox.getChildren().get(4); // Text info will be at index 5
					info.setText("Animation is currently paused.");
				}
				/* else String playPause = "Pause" animation is currently paused
				 * Will need to play animation and change playPause to "Play", then change button text to match. */
				else {
					VBox thisVBox = (VBox) mainBorder.getLeft(); // get leftVBox from mainBorder.
					Button playPauseBTN = (Button) thisVBox.getChildren().get(2); // playPause button is always at index 2 currently
					playPauseBTN.setText("PAUSE"); // update button text to reflect new String
					playPause = "Play";
					timeline.play();
					//edit Text info, info changes if animation is stopped.
					Text info = (Text) thisVBox.getChildren().get(4); // Text info will be at index 5
					info.setText("Animation is currently playing.");
				}
			}
		});
		
		//add stop button
		Button stopBTN = new Button();
		stopBTN.setText("STOP");
		stopBTN.setPrefWidth(150);
		buttons.getChildren().add(stopBTN); // add button to VBox
		//set action for stopBTN
		stopBTN.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				BTNpress.play(pressVolume); // BTNpress sound
				/* This method will need to update Play pause button to ensure it acts as a play BTN when stopBTN is used. & update isStopped.
				 * Once done update Text at index 5 */
				VBox thisVBox = (VBox) mainBorder.getLeft(); // get leftVBox from mainBorder.
				Button playPauseBTN = (Button) thisVBox.getChildren().get(2); // playPause button is always at index 2 currently
				playPauseBTN.setText("PLAY"); // update button text to reflect new String
				playPause = "Pause"; // update playPause String
				// Then stop.
				timeline.stop();
				clearAnimation();
				// Edit Text info
				Text info = (Text) thisVBox.getChildren().get(4); // Text info will be at index 5
				info.setText("Animation is currently stopped.");
			}
		});
		
		/* add text detailing functions of buttons to user. */
		Text info = new Text("Animation is currently stopped.");
		info.setFill(Color.GREY);
		buttons.getChildren().add(info);
		
		/* add separating line underneath Stop button.
		 * newLine creates a rectangle changes w/h and color then returns to add to buttons */
		buttons.getChildren().add(newLine()); // add the Rectangle to VBox buttons
		
		/* Add drop down menu to decide what element to add to animation when addBTN is used. */
		ComboBox<String> ddMenu = new ComboBox<String>();
		ObservableList<String> options = FXCollections.observableArrayList("Butterfly", "Caterpillar", "Flower", "Shrub", "Wasp");
		ddMenu.setItems(options);
		ddMenu.getSelectionModel().selectFirst();
		ddMenu.setPrefWidth(150);
		buttons.getChildren().add(ddMenu);
		ddMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				BTNpress.play(pressVolume); // BTNpress sound
				addElement = ddMenu.getValue();
				VBox thisVBox = (VBox) mainBorder.getLeft(); // get leftVBox from mainBorder.
				Button addBTN = (Button) thisVBox.getChildren().get(8); // get addBTN to update text, always at index 8 at the moment
				addBTN.setText("Add " + addElement);
			}
		});
		
		/* Add slider to select amount of elements to add to World. */
		Slider addSlider = new Slider(1, 10, 10);
		addSlider.setShowTickMarks(true); //enable tick marks
		addSlider.setShowTickLabels(true); //enable labels on slider
		addSlider.setMajorTickUnit(1); //display ticks of 10
		addSlider.setMinorTickCount(1); //add minor tick count of 5
		addSlider.setBlockIncrement(1); // set value of block increment
		addSlider.setPrefWidth(150);
		buttons.getChildren().add(addSlider);
		// Add listener for slider
		addSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> 
				observable , Number oldValue, Number newValue) {
				addNumber = newValue.intValue();
			}
			
		});
		
		/* BTN to add elements into the World. 
		 * I set this to require timeline.stop() to work so there was a point to having a stop button. */
		Button addBTN = new Button();
		addBTN.setText("Add " + addElement);
		addBTN.setPrefWidth(150);
		buttons.getChildren().add(addBTN); // add button to VBox
		//set action for addBTN
		addBTN.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				BTNpress.play(pressVolume); // BTNpress sound
				addElements(addElement, addNumber); // add elements
			}
		});
		
		/* add separating line underneath add button.
		 * newLine creates a rectangle changes w/h and color then returns to add to buttons */
		buttons.getChildren().add(newLine()); // add the Rectangle to VBox buttons
		
		/* add text detailing volume slider below. */
		Text bgInfo = new Text("Backgorund music volume:");
		info.setFill(Color.GREY);
		buttons.getChildren().add(bgInfo);
		
		/* Add slider to control BG music. */
		Slider BGMSlider = new Slider(0, 1, 0.5);
		BGMSlider.setShowTickMarks(true); //enable tick marks
		BGMSlider.setShowTickLabels(true); //enable labels on slider
		BGMSlider.setMajorTickUnit(0.1); //display ticks 
		BGMSlider.setMinorTickCount(1); //add minor tick count of 5
		BGMSlider.setBlockIncrement(0.2); // set value of block increment
		BGMSlider.setPrefWidth(150);
		buttons.getChildren().add(BGMSlider);
		// Add listener for slider
		BGMSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> 
				observable , Number oldValue, Number newValue) {
				bgmVolume = newValue.doubleValue();
				melody.setVolume(bgmVolume);
			}
			
		});
		
		/* add text detailing volume slider below. */
		Text pressInfo = new Text("Button press volume:");
		info.setFill(Color.GREY);
		buttons.getChildren().add(pressInfo);
		
		/* Add slider to control BG music. */
		Slider pressSlider = new Slider(0, 1, 0.5);
		pressSlider.setShowTickMarks(true); //enable tick marks
		pressSlider.setShowTickLabels(true); //enable labels on slider
		pressSlider.setMajorTickUnit(0.1); //display ticks 
		pressSlider.setMinorTickCount(1); //add minor tick count of 5
		pressSlider.setBlockIncrement(0.2); // set value of block increment
		pressSlider.setPrefWidth(150);
		buttons.getChildren().add(pressSlider);
		// Add listener for slider
		pressSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> 
				observable , Number oldValue, Number newValue) {
				pressVolume = newValue.doubleValue();
				BTNpress.setVolume(pressVolume);
			}
			
		});
		
		/* add separating line underneath audio control.
		 * newLine creates a rectangle changes w/h and color then returns to add to buttons */
		buttons.getChildren().add(newLine()); // add the Rectangle to VBox buttons
		
		//add quit button
		Button quitBTN = new Button();
		quitBTN.setText("QUIT");
		quitBTN.setPrefWidth(150);
		buttons.getChildren().add(quitBTN); // add button to VBox
		//set action for stopBTN
		quitBTN.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
			}
		});
		
		return buttons;
	}
	
	private Group addMapPane(Timeline timeline) { // atm just draws a green rectangle as backdrop for animation
		// create new world with double width, double height
		this.theWorld = new World(750, 550); 
		/* Create group 'map' with rectangle to act as backdrop for animation. */
		Group map = new Group();
		map.maxWidth(800);
		map.maxHeight(600);
		Rectangle BG = new Rectangle();
		BG.setX(0);
		BG.setY(0);
		BG.setWidth(750);
		BG.setHeight(550);
		BG.setFill(Color.GREEN);
		map.getChildren().add(BG);

		
		// create KeyFrame for animating theWorld
		KeyFrame frame = new KeyFrame(Duration.millis(15), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
				/* Remove any dead Bugs/Plants from animation & check if Butterflies/Caterpillars are ready to transform. */
				refreshWorldList(map);
				
				/* Run for each on all Bugs and update their x and y in the world. */
				for (Bug b : theWorld.getBugList()) {
					// getDestination in Bug requires World bugList, World plantList, World width and World height.
					b.getDestination(theWorld.getBugList(), theWorld.getPlantList(), theWorld.getWidth(), theWorld.getHeight());
					//once destination decided move bugs ImageView
					ImageView bugImageView = b.getBugImage();
					bugImageView.setX(b.getX());
					bugImageView.setY(b.getY());
				}	
				
				/* Run for each on all Plants and update their size/sprite in the world. */
				for (Plant p : theWorld.getPlantList()) {
					p.growPlant(); // Increase plants size every frame. Will not increase if size = 0 to be removed next frame.
					ImageView plantImageView = p.getPlantImage();
					/* Update image size x and y get adjusted in eat and grow plant methods
					 * x and y are updated to account for changes in image size. */
					if (p.getType().equals("Shrub")) { // shrubs x and y + size changes so need type filter
						plantImageView.setX(p.getX());
						plantImageView.setY(p.getY());
						plantImageView.setFitWidth(p.getSize());
						plantImageView.setFitHeight(p.getSize());
					}
					else { // flowers just adjust size.
						plantImageView.setFitWidth(p.getSize());
						plantImageView.setFitHeight(p.getSize());
					}
				}
				/* This if statement uses counter to add new flowers and shrubs into the world at set intervals. */
				if (counter == 200) {
					addElements("Flower", 2);
				}
				else if (counter == 400) {
					addElements("Flower", 2);
					addElements("Shrub", 2);
				}
				counter ++;
			}
		});
		//Set up timeline 
		timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
		timeline.getKeyFrames().add(frame);
		timeline.stop();
		
		// return map to BorderPane.
		return map;
	}
	
	
	private VBox rightBorder() {
		/* rightBorder is only used as border around map in UI. */
		VBox border = new VBox();
		border.setPrefSize(5, 500);
		border.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
		return border;
	}
	
	
	private void clearAnimation() {
		/* Get group map from center and remove all ImageViews from centerPane group
		 * using bugList and plantList in theWorld. 
		 * Clear both arrayLists in World afterwards using resetWorld. */
		Group map = (Group) (mainBorder.getCenter());
		List<Bug> bugsToRemove = theWorld.getBugList(); // Get list of Bugs to remove
		for (Bug b : bugsToRemove) { // run for each loop, get Bug ImageView and remove from Group map
			ImageView IV = b.getBugImage();
			map.getChildren().remove(IV);
		}
		List<Plant> plantsToRemove = theWorld.getPlantList(); // Get list of Plants to remove
		for (Plant p : plantsToRemove) { // run for each loop, get Plant ImageView and remove from Group map
			ImageView IV = p.getPlantImage();
			map.getChildren().remove(IV);
		}
		theWorld.resetWorld(); // clear both Lists in the World
	}

	
	private void refreshWorldList(Group map) {
		/* A method to remove and Bugs or Plants that have 'died'. 
		 * Also remove Caterpillars that have evolved and replace with new butterfly.
		 * Add baby caterpillars if butterfly has one. */
		List<Bug> newBugList = new ArrayList<Bug>(); // Create an empty List to hold any Bugs that are not dead
		for (Bug b : theWorld.getBugList()) {
			if (b.getSpecies().equals("Butterfly")) {
				if (((Butterfly) b).babyCount >= 1) {
					((Butterfly) b).babyCount = 0;
					Bug newb = new Caterpillar(b.x-10, b.y-10);
					newBugList.add(newb); // add baby
					newBugList.add(b); // add parent
					ImageView bugImageView = newb.getBugImage();
					bugImageView.setX(newb.getX());
					bugImageView.setY(newb.getY());
					((Group) mainBorder.getCenter()).getChildren().add(bugImageView); // Add image to animation.
				}
				else if (b.getHunger() < 0 && b.getAge() > 2500) {
					map.getChildren().remove(b.getBugImage()); // remove Bug from Group map
				}
				else {
					// if hadChild is false add Butterfly to new List
					newBugList.add(b); // Add Bug to new List
				}
			}
			else if (b.getHunger() > 0 && b.getAge() < 2500) { // if hunger > 0 & if age < 2500 turns bug still alive
				
				if (b.getSpecies().equals("Caterpillar")) {
					 /* if transformBug is true remove Bug from Group map and add new butterfly. */
					if (((Caterpillar) b).getTransformBug()) {
						map.getChildren().remove(b.getBugImage());
						Bug newb = new Butterfly(b.getX(), b.getY());
						newBugList.add(newb);
						ImageView bugImageView = newb.getBugImage();
						bugImageView.setX(newb.getX());
						bugImageView.setY(newb.getY());
						((Group) mainBorder.getCenter()).getChildren().add(bugImageView); // Add image to animation.
					}
					else {// if isTransforming false add Caterpillar to new List
						newBugList.add(b); // Add Bug to new List
					}
				}
				
				else newBugList.add(b); // Add Bug to new List
			}
			else { // else Bug is dead
				map.getChildren().remove(b.getBugImage()); // remove Bug from Group map 
			}
		}
		theWorld.setBugList(newBugList); // Reset List in World with only Bugs that are still alive
		
		/* Repeat for Plants. */
		List<Plant> newPlantList = new ArrayList<Plant>();
		for (Plant p : theWorld.getPlantList()) {
			if (p.getSize() > 5) {
				newPlantList.add(p);
			}
			else {
				map.getChildren().remove(p.getPlantImage());
			}
		}
		theWorld.setPlantList(newPlantList);
	}
	
	/* Quick method to auto-populate world on spawn. */
	private void setWorldLife(Group map) {
		List<Plant> newFlowers = theWorld.addPlants("Flower", 30);
		for (Plant p : newFlowers) {
			ImageView plantImageView = p.getPlantImage();
			plantImageView.setPreserveRatio(true);
			plantImageView.setX(p.getX());
			plantImageView.setY(p.getY());
			plantImageView.setFitWidth(p.getSize());
			plantImageView.setFitHeight(p.getSize());
			((Group) mainBorder.getCenter()).getChildren().add(plantImageView); // Add image to animation.
		}
		List<Plant> newShrubs = theWorld.addPlants("Shrub", 30);
		for (Plant p : newShrubs) {
			ImageView plantImageView = p.getPlantImage();
			plantImageView.setPreserveRatio(true);
			plantImageView.setX(p.getX());
			plantImageView.setY(p.getY());
			plantImageView.setFitWidth(p.getSize());
			plantImageView.setFitHeight(p.getSize());
			((Group) mainBorder.getCenter()).getChildren().add(plantImageView); // Add image to animation.
		}
		List<Bug> newButterflies = theWorld.addBugs("Butterfly", 20);
		for (Bug b : newButterflies) {
			ImageView bugImageView = b.getBugImage();
			bugImageView.setX(b.getX());
			bugImageView.setY(b.getY());
			((Group) mainBorder.getCenter()).getChildren().add(bugImageView); // Add image to animation.
		}
		List<Bug> newCaterpillars = theWorld.addBugs("Caterpillar", 10);
		for (Bug b : newCaterpillars) {
			ImageView bugImageView = b.getBugImage();
			bugImageView.setX(b.getX());
			bugImageView.setY(b.getY());
			((Group) mainBorder.getCenter()).getChildren().add(bugImageView); // Add image to animation.
		}
		List<Bug> newWasps = theWorld.addBugs("Wasp", 3);
		for (Bug b : newWasps) {
			ImageView bugImageView = b.getBugImage();
			bugImageView.setX(b.getX());
			bugImageView.setY(b.getY());
			((Group) mainBorder.getCenter()).getChildren().add(bugImageView); // Add image to animation.
		}
	}
	
	private void addElements(String addElement, int addNumber) {
		/* determine type and number of Bugs/Plants to create. addBugs/Plants methods return a list of new elements added
		 * those new element need to be added to CenterPane. */
		if (addElement.equals("Flower") || addElement.equals("Shrub")) {
			List<Plant> newPlants = theWorld.addPlants(addElement, addNumber);
			for (Plant p : newPlants) {
				ImageView plantImageView = p.getPlantImage();
				plantImageView.setPreserveRatio(true);
				plantImageView.setX(p.getX());
				plantImageView.setY(p.getY());
				plantImageView.setFitWidth(p.getSize());
				plantImageView.setFitHeight(p.getSize());
				((Group) mainBorder.getCenter()).getChildren().add(plantImageView); // Add image to animation.
			}
		}
		
		else if (addElement.equals("Butterfly") || addElement.equals("Caterpillar") || addElement.equals("Wasp")) {
			List<Bug> newBugs = theWorld.addBugs(addElement, addNumber);
			for (Bug b : newBugs) {
				ImageView bugImageView = b.getBugImage();
				bugImageView.setX(b.getX());
				bugImageView.setY(b.getY());
				((Group) mainBorder.getCenter()).getChildren().add(bugImageView); // Add image to animation.
			}
		}
	}
	
	private Rectangle newLine() {
		Rectangle line = new Rectangle();
		line.setWidth(180);
		line.setHeight(2);
		line.setFill(Color.GREY);
		return line;
	}
	
	
	public static void main(String[] args) {
		launch();

	}
}
