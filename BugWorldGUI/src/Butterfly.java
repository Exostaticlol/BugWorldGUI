import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Butterfly extends Bug {
	
	/* A wasp will trigger isPrey when hunting a butterfly. */
	protected boolean isPrey; 
	/* If a Bug has a child hadChild is true, Bug has 400 turn buffer before resetting hadChild to false. */
	private boolean hadChild;
	/* Count for hadChild. Bug cannot move for 50 turns, then Bug cannot have child until 400 turns. */
	private int hadChildCount = 0;
	/* Only 1 Bug will change this value when having child. 
	 * GUI uses this value to determine when to add a caterpillar, and resets to 0 when done. */
	public int babyCount = 0;
	/* If Butterfly targets a mate they will hold a reference to each other. */
	private Butterfly isMate;

	public Butterfly(double x, double y) { // double x and y are randomly generated when created by world or set by evolving caterpillar.
		/* super requires (species, diet, x, y, speed, hungerMax, sightRange. 
		 * x and y are randomly generated on spawn. Other conditions are hardcoded. */
		super("Butterfly", "Flower", x, y, 1.4, 500, 75);
		hunger = 400; // spawn with 400 hunger
	}

	/* Butterfly has two color variants these are randomly decided on spawn by a Random between 1-2.
	 * Super holds two images (open, closed) for sprite animation and hold ImageView (bugImage) representing the Butterfly on the map. */
	protected void setBugImage() { 
		Random rand = new Random();
		int n = rand.nextInt(3) + 1;
		/* Set images and ImageView for Bug. */
		String imageUrl = "/images/butterfly" + n + "-open.png"; 
		Image imageOpen = new Image(imageUrl); 
		open = imageOpen; // Store first image in super
		imageUrl = "/images/butterfly" + n + "-closed.png";
		Image imageClosed = new Image(imageUrl);
		closed = imageClosed; // store 2nd image in super
		this.bugImage = new ImageView(open); // update bug image using first image.
	}
	
	/* Uses field in super imageRefresh to count number of turns that have passed and update and rest counter. */
	protected void updateBugImage() {
		imageRefresh += 1; // add 1 to imageRefresh each turn
		Image currentImage = bugImage.getImage(); // get bugs current displayed image
		if (currentImage == open && imageRefresh > 15) { // check if image is currently open and if imageRefresh has hit 20 turns.
			bugImage.setImage(closed); // swap image
			imageRefresh = 0; // reset imageRefresh
		}
		else if (currentImage == closed && imageRefresh > 10) { // check if image is currently open and if imageRefresh has hit 10 turns.
			bugImage.setImage(open); // swap image
			imageRefresh = 0; // reset imageRefresh
		}
	}
	
	
	public void getDestination(List<Bug> bugList, List<Plant> plantList, double width, double height) {
		/* getDestination method for Butterflies*/
		ageUp(); // increase Bug age by 1
		this.hunger -= 1.5; // Decrease hunger every turn
		this.updateBugImage(); // Updates Bug sprite for animation
		
		/* Check if Bug recently hadChild, increase count. */
		if (hadChild) hadChildCount += 1;
		if (hadChildCount >= 400) {
			hadChildCount = 0;
			hadChild = false;
		}
		/* If count < 50 Bug will not move. All other methods inside else statement. */
		if (hadChild && hadChildCount < 50) {
			// NOTHING HERE AT ALL BUG WILL SIT FOR 50 TURNS
		}
		
		else { // Rest of Bugs algorithm in here
			Plant p = this.getBestPlant(plantList); // Looks for closest flower returns null if none in range.
			Bug predator = this.getThreat(bugList); // returns closest Wasp with isHunting set to true, returns null if none in sightRange
			
			/* Check if Prey was hunted last turn but Hunt is over. If hunt is over isPrey is false. */
			if (predator == null) isPrey = false;
			
			/* Check if this bug and Mate are still viable to mate. Set isMate to null for both Bugs. */
			if (isMate != null) { 
				if (!(seekingMate() && isMate.seekingMate())) { // Both butterflies must return true on seeking mate for condition to remain valid.
					/* set both bugs isMate to null. */
					isMate.setIsMate(null);
					this.isMate = null;
				}
			}
			
			/* If bugs hunger is less than hungerMax*0.3 Bug needs food and will ignore every other action.
			 * Plant p cannot be null, Plant p will be null if not Plants in bugs sightRange. */
			if (hunger < hungerMax*0.3 && p != null) {
				if (bugOnPlant(p)) { // if Bug is currently on destination Plant (p) run method eatPlant
					// eatPlant will return false if Plant size = 0.
					if(!eatPlant(p)) keepMoving(width, height); // if eatPlant returns false Bug will keep moving.
				}
				else { // if bug not on plant
					moveTowardsPlant(p); // move towards plant
					
					/* Bug gets one more change to eat after moving. */
					if (bugOnPlant(p)) { // if Bug is currently on destination Plant (p) run method eatPlant
						// eatPlant will return false if Plant size = 0.
						if(!eatPlant(p)) keepMoving(width, height); // if eatPlant returns false Bug will keep moving.
					}
				}
			}
			
			
			/* Check if Bug is being attacked first. If bug isPrey it has been captured and cannot move, all other actions are contained within this. */
			else if (!isPrey) {
				/* getBestMate returns closest butterfly that is a possible mate, get best mate will return null is no Butterfly in range. */
				Butterfly mate = this.getBestMate(bugList); 
				
				/* Run away if hunting Wasps are near. */
				if (predator != null) { // if predator is null there are no Wasps near that are hunting.
					/* If predator is near Butterfly will drop all other actions to run. */
					if (isMate != null) isMate = null;
					isEating = false;
					/* Then run. */
					run(predator, width, height); // requires closest predator bug, width and height of world.
				}
				
				/* If bug still has target Bug move towards them and mate. */
				else if (isMate != null) {
					if (bugOnBug(isMate)) haveChild(isMate); // if Bug and Bug have made contact have child sets both hadChild to true.
					else moveTowardsMate(isMate);
				}
				
				/* Check if bug was eating last turn with boolean isEating & destination Plant (p). p cannot be null
				 * If Bug was eating last turn and no Plants in range, then Plant size hit 0 and was removed.
				 * isEating will be true if a Bug used method eatPlant last turn and Bugs hunger < 495 */
				else if (isEating && p != null) {
					/* Check if a Bug is currently on a destination Plant (p). */
					if (bugOnPlant(p)) { // if Bug is currently on destination Plant (p) run method eatPlant
						if(!eatPlant(p)) keepMoving(width, height); // if eatPlant returns false Bug will keep moving.
					}
					else { // if bug is no longer on plant
						moveTowardsPlant(p); // move towards plant
						/* Bug gets one more change to eat after moving. */
						if (bugOnPlant(p)) { // if Bug is currently on destination Plant (p) run method eatPlant
							if(!eatPlant(p)) keepMoving(width, height); // if eatPlant returns false Bug will keep moving.
						}
					}
				}
				
				/* Bug can look for mate if seeking mate is true for this Butterfly and target butterfly chosen by getBestMate.
				 * If Butterfly is age and hunger is high enough seekinMate will return true. */
				else if (mate != null && seekingMate() && mate.seekingMate()) {
					/* Move towards mate set Bugs direction towards mate and also updates both Bugs 'isMate'.
					 * once isMate has a Butterfly bugs will use different action path. */
					moveTowardsMate(mate); 
				}
				
				/* Check if a Bug is currently hungry, Bug is hungry if double hunger < 350.
				 * Bug also needs confirmation of Plants in sight range for method so Plant p cannot be null */
				else if (hunger < hungerMax*0.7 && p != null) {
					/* Check if a Bug is currently on a destination Plant (p).
					 * Plant cannot be null for method bugOnPlant */
					if (bugOnPlant(p)) { // if Bug is currently on destination Plant (p) run method eatPlant
						if(!eatPlant(p)) keepMoving(width, height); // if eatPlant returns false Bug will keep moving.
					}
					/* else if bugOnPlant returns false Bug will move towards Destination Plant (p). */
					else moveTowardsPlant(p);
				}
				/* If Bug had no destination Plant then Bug will keepMoving. */
				else keepMoving(width, height);
			}
		}
	}
	
	private void haveChild(Butterfly mate) {
		this.hadChild = true;
		Random rand = new Random();
		if (rand.nextInt(10)+1 > 9) babyCount += 1; // lower chance to have baby to stop over population lol
		mate.hadChild = true; // isMate hadChild = true. But does not increase babyCount for mate
	}

	private boolean seekingMate() {
		if (hunger > hungerMax*0.9 && age > 1000 && !hadChild) return true;
		return false;
	}
	
	private Butterfly getBestMate(List<Bug> bugList) {
		Butterfly mate = null;
		for (Bug b : bugList) {
			if (bugInRange(b) && b.getHunger() > 450 && b.getSpecies().equals("Butterfly")) {
				if (mate == null) mate = (Butterfly) b;
				double xdiff = this.x - b.getX();
				if (xdiff < 0) xdiff = -xdiff; // convert to positive number if it was negative
				double ydiff = this.y - b.getY();
				if (ydiff < 0) ydiff = -ydiff; // convert to positive number if it was negative
				// repeat for current bestPlant
				double BPxdiff = this.x - mate.getX();
				if (BPxdiff < 0) BPxdiff = -BPxdiff; // convert to positive number if it was negative
				double BPydiff = this.y - mate.getY();
				if (BPydiff < 0) BPydiff = -BPydiff; // convert to positive number if it was negative
				// decide which is overall closer
				if((xdiff+ydiff) < (BPxdiff+BPydiff)) mate = (Butterfly) b;
			}
		}
		return mate;
	}
	
	// decide which Plant is closest to Bugs current x and y coordinates
	protected Plant getBestPlant(List<Plant> plantList) {
		Plant bestPlant = null;
		for (Plant p : plantList) {
			if (this.plantInRange(p) && p.getSize() > 1 && p.getType().equals("Flower")) {
				if (bestPlant == null) bestPlant = p;
				// get difference for each coordinate to compare
				double xdiff = this.x - p.getX();
				if (xdiff < 0) xdiff = -xdiff; // convert to positive number if it was negative
				double ydiff = this.y - p.getY();
				if (ydiff < 0) ydiff = -ydiff; // convert to positive number if it was negative
				// repeat for current bestPlant
				double BPxdiff = this.x - bestPlant.getX();
				if (BPxdiff < 0) BPxdiff = -BPxdiff; // convert to positive number if it was negative
				double BPydiff = this.y - bestPlant.getY();
				if (BPydiff < 0) BPydiff = -BPydiff; // convert to positive number if it was negative
				// decide which is overall closer
				if((xdiff+ydiff) < (BPxdiff+BPydiff)) bestPlant = p;
			}
		}
		return bestPlant;
	}
	
	private Bug getThreat(List<Bug> bugList) {
		/* Create new list with only wasps. */
		List<Wasp> badList = new ArrayList<Wasp>();
		for (Bug b : bugList) {
			if (b.getSpecies().equals("Wasp")) badList.add((Wasp) b);
		}
		Bug predator = null;
		for (Wasp w : badList) {
			if (this.bugInRange(w) && w.getIsHunting()) {
				if (predator == null) predator = w;
				// get difference for each coordinate to compare
				double xdiff = this.x - w.getX();
				if (xdiff < 0) xdiff = -xdiff; // convert to positive number if it was negative
				double ydiff = this.y - w.getY();
				if (ydiff < 0) ydiff = -ydiff; // convert to positive number if it was negative
				// repeat for current bestPlant
				double BPxdiff = this.x - predator.getX();
				if (BPxdiff < 0) BPxdiff = -BPxdiff; // convert to positive number if it was negative
				double BPydiff = this.y - predator.getY();
				if (BPydiff < 0) BPydiff = -BPydiff; // convert to positive number if it was negative
				// decide which is overall closer
				if((xdiff+ydiff) < (BPxdiff+BPydiff)) predator = w;
			}
		}
		return predator;
	}
	
	protected boolean plantInRange(Plant p) {
		//check if plant is within a bugs sightRange
		if (this.x - p.getX() < sightRange && this.x - p.getX() > -sightRange && 
				this.y - p.getY() < sightRange && this.y - p.getY() > -sightRange) {
			return true;
		}
	return false;
	}
	
	protected boolean bugInRange(Bug b) {
		//check if bug is within a bugs sightRange
		if (this.x - b.getX() < sightRange && this.x - b.getX() > -sightRange && 
				this.y - b.getY() < sightRange && this.y - b.getY() > -sightRange) {
			return true;
		}
	return false;
	}
	
	protected boolean eatPlant(Plant p) {
		/* Method for a Bug 'eating a plant'. Return true if Bug eats, False if Bug is full or cannot eatPlant.
		 * if a Bug is full (hunger = 500) Bug no longer needs to eat and boolean isEating will changed to false.
		 * else if Bug is still hungry Bug will attempt to eat plant, if Plant size hits 0 method p.eatPlant will return false
		 * if eatPlant returns false Bug will move on. */
		if (hunger >= hungerMax) { // check if bug is 'full'.
			hunger = hungerMax;
			isEating = false; // set isEating to false if Bug does not Eat
			return false; // Bug did not 'eat', return false.
		}
		else { 
			if (p.eatPlant()) { // eatPlant is true if Plant is 'eaten'.
				hunger += 7.5; // increase Bug hunger
				isEating = true; // set isEating to true as Bug did eat
				return true; // return true as Bug did eat.
			}
			else { // if Plant size < 0 Plant cannot be eaten.
				isEating = false; // set isEating to false if Bug cannot Eat
				return false; // Bug did not 'eat', return false.
			}
		}
	}
	
	protected void keepMoving(double width, double height) {
		/* Bug will continue moving until interrupted. */
		// Check for edges of map and update direction (dx, dy) if Bug hits edge of map.
		if (this.x < bugImageSize || this.x + bugImageSize > width) dx = -dx;
		if (this.y <= bugImageSize || this.y + bugImageSize > height) dy = -dy;
		
		// Adjust x and y by final direction
		this.x += dx;
		this.y += dy;
	}

	protected boolean bugOnPlant(Plant p) {
		if (Math.abs(this.x - p.getX()) < p.getSize() && Math.abs(this.y - p.getY()) < p.getSize()) return true;
		return false;
	}
	
	protected boolean bugOnBug(Bug b) {
		if (Math.abs(this.x - b.getX()) < b.getBugImageSize()*0.9 && Math.abs(this.y - b.getY()) < b.getBugImageSize()*0.9) return true;
		return false;
	}
	
	protected void moveTowardsPlant(Plant p) {
		/* Determine the direction of a Plant and set Bug x and y to move towards it. */
		Double xDest = p.getX();
		Double yDest = p.getY();
		/* if statements to confirm dx and dy are in correct direction
		 * updates direction if wrong. */
		if ((this.x > xDest && dx > 0) || (this.x < xDest && dx < 0)) dx = -dx;
		if ((this.y > yDest && dy > 0) || (this.y < yDest && dy < 0)) dy = -dy;
		
		//set final movement for Bug
		this.x += dx;
		this.y += dy;
	}
	
	protected void moveTowardsMate(Butterfly b) {
		this.isMate = b;
		b.setIsMate(this);
		/* Determine the direction of a Plant and set Bug x and y to move towards it. */
		Double xDest = b.getX();
		Double yDest = b.getY();
		/* if statements to confirm dx and dy are in correct direction
		 * updates direction if wrong. */
		if ((this.x > xDest && dx > 0) || (this.x < xDest && dx < 0)) dx = -dx;
		if ((this.y > yDest && dy > 0) || (this.y < yDest && dy < 0)) dy = -dy;
		
		//set final movement for Bug
		this.x += dx;
		this.y += dy;
	}
	
	private void run(Bug predator, double width, double height) {
		/* Determine the direction of the predator and set Bug x and y to move away from it. */
		Double xDest = predator.getX();
		Double yDest = predator.getY();
		/* if statements to confirm dx and dy are in correct direction
		 * updates direction if wrong. */
		if ((this.x > xDest && dx < 0) || (this.x < xDest && dx > 0)) dx = -dx;
		if ((this.y > yDest && dy < 0) || (this.y < yDest && dy > 0)) dy = -dy;
		
		// make sure bug is not going to hit edge of map.
		if (this.x < bugImageSize || this.x + bugImageSize > width) dx = -dx;
		if (this.y <= bugImageSize || this.y + bugImageSize > height) dy = -dy;
		
		//set final movement for Bug
		this.x += dx;
		this.y += dy;
	}
	
	public void setIsPrey(boolean prey) {
		isPrey = prey;
	}
	
	public boolean getHadChild() {
		return hadChild;
	}
	
	public void setHadChild() {
		hadChild = false;
	}

	public Bug getIsMate() {
		return isMate;
	}

	public void setIsMate(Butterfly isMate) {
		this.isMate = isMate;
	}
}
