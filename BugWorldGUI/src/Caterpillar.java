import java.util.List;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Caterpillar extends Bug {
	// caterpillars will not age up.
	protected boolean isPrey; // implement later
	protected boolean cocoon = false;
	private int cocoonCount = 0;
	private boolean transformBug = false;
	
	private Image cocoon1, cocoon2, cocoon3;

	public Caterpillar(double x, double y) { // double x and y are randomly generated when created by world
		/* super requires (species, diet, x, y, speed, hungerMax, sightRange. 
		 * x and y are randomly generated on spawn. Other conditions are hardcoded. */
		super("Caterpillar", "Shrub", x, y, 0.4, 1500, 65);
		hunger = 750; // if a caterpillar gets too 1000 hunger they become butterfly. Default at 750 on spawn
	}

	protected void setBugImage() { 
		/* Set images and ImageView for Bug. */
		String imageUrl = "/images/caterpillar1-long.png"; 
		Image imageOpen = new Image(imageUrl); 
		open = imageOpen;
		imageUrl = "/images/caterpillar1-short.png";
		Image imageClosed = new Image(imageUrl);
		closed = imageClosed;
		this.bugImage = new ImageView(open);
		/* Caterpillar needs cocoon images. */
		imageUrl = "/images/cocoon1-1.png"; 
		Image cocoon = new Image(imageUrl); 
		cocoon1 = cocoon;
		
		imageUrl = "/images/cocoon1-2.png"; 
		cocoon = new Image(imageUrl); 
		cocoon2 = cocoon;
		
		imageUrl = "/images/cocoon1-3.png"; 
		cocoon = new Image(imageUrl); 
		cocoon3 = cocoon;
	}
	
	protected void updateBugImage() {
		imageRefresh += 1; // add 1 to imageRefresh each turn
		Image currentImage = bugImage.getImage(); // get bugs current displayed image
		if (!cocoon) {
			if (currentImage == open && imageRefresh > 20) { // check if image is currently open and if imageRefresh has hit 20 turns.
				bugImage.setImage(closed); // swap image
				imageRefresh = 0; // reset imageRefresh
			}
			else if (currentImage == closed && imageRefresh > 20) { // check if image is currently open and if imageRefresh has hit 10 turns.
				bugImage.setImage(open); // swap image
				imageRefresh = 0; // reset imageRefresh
			}
		}
		else { // IF BUG IS COCOON
			if (imageRefresh == 25) bugImage.setImage(cocoon2);
			if (imageRefresh == 30) bugImage.setImage(cocoon1);
			if (imageRefresh == 45) bugImage.setImage(cocoon3);
			if (imageRefresh == 50) bugImage.setImage(cocoon1);
			if (imageRefresh == 75) bugImage.setImage(cocoon2);
			if (imageRefresh == 85) bugImage.setImage(cocoon1);
			if (imageRefresh == 90) bugImage.setImage(cocoon3);
			if (imageRefresh == 95) bugImage.setImage(cocoon1);
		}
	}
	
	public void getDestination(List<Bug> bugList, List<Plant> plantList, double width, double height) {
		
		
		/* Plant p is destination Plant, getBestPlant will return null if no Plants in sightRange.
		 * If Bug was eating last turn and no Plants in range, then Plant size hit 0 and was removed. */
		Plant p = this.getBestPlant(plantList);
		
		/*if Bug is currently cocoon return after updating count. */
		if (cocoon) {
			cocoonCount += 1;
			/*  set transform to true.
			 * GUI will delete bug and add new butterfly to transform. */
			if (cocoonCount > 750) transformBug = true; 
			return;
		}
		
		
		/* Check if caterpillar has eaten enough to turn into Butterfly. */
		if (hunger > hungerMax) {
			if (p != null && bugOnPlant(p)) {
				updateCocoon();
			}
			else keepMoving(width, height);
		}
		/* getDestination method for Butterflies*/
		this.hunger -= 0.5; // Decrease hunger every turn
		this.updateBugImage(); // Updates Bug sprite for animation
		
		/* Check if Bug is being attacked first.
		 * Bug is being attacked if boolean isPrey is true.
		 */
		//add later
		
		/* Check if bug was eating with boolean isEating & that destination Plant (p) is not null
		 * If Bug was eating last turn and no Plants in range, then Plant size hit 0 and was removed.
		 * isEating will be true if a Bug used method eatPlant last turn and Bugs hunger < 495 */
		if (isEating && p != null) {
			/* Check if a Bug is currently on a destination Plant (p).
			 * Plant cannot be null for method bugOnPlant */
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
		
		/* Check if a Bug is currently hungry, Bug is hungry if double hunger < 350.
		 * Bug also needs confirmation of Plants in sight range for method so Plant p cannot be null */
		else if (hunger < hungerMax && p != null) {
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
	
	// decide which Plant is closest to Bugs current x and y coordinates
	protected Plant getBestPlant(List<Plant> plantList) {
		Plant bestPlant = null;
		for (Plant p : plantList) {
			if (this.plantInRange(p) && p.getSize() > 1 && p.getType().equals("Shrub")) {
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
	
	protected boolean plantInRange(Plant p) {
		//check if plant is within a bugs sightRange
		if (this.x - p.getX() < sightRange && this.x - p.getX() > -sightRange && 
				this.y - p.getY() < sightRange && this.y - p.getY() > -sightRange) {
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
			isEating = false; // set isEating to false if Bug does not Eat
			return false; // Bug did not 'eat', return false.
		}
		else { 
			if (p.eatPlant()) { // eatPlant is true if Plant is 'eaten'.
				hunger += 2.5; // increase Bug hunger
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
		// (p.getSize()*0.4) represents current size of Plant drawn in animation.
		if (Math.abs(this.x - p.getX()) < (p.getSize()*0.5) && Math.abs(this.y - p.getY()) < (p.getSize()*0.5)) return true;
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
	
	public boolean getTransformBug() {
		return transformBug;
	}
	
	private void updateCocoon() {
		cocoon = true;
		bugImage.setImage(cocoon1);
	}
}