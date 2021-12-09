import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Wasp extends Bug {
	
	public int restMeter; // tracks if the bug wants to rest on a Plant or not
	public boolean isHunting = false, isResting = false;

	public Wasp(double x, double y) { // double x and y are randomly generated when created by world
		/* super requires (species, diet, x, y, speed, hungerMax, sightRange. 
		 * x and y are randomly generated on spawn. Other conditions are hardcoded. */
		super("Wasp", "Butterfly", x, y, 1.3, 750, 100);
	}
	
	protected void setBugImage() { 
		/* Set images and ImageView for Bug. */
		String imageUrl = "/images/wasp-open.png"; 
		Image imageOpen = new Image(imageUrl); 
		open = imageOpen;
		imageUrl = "/images/wasp-closed.png";
		Image imageClosed = new Image(imageUrl);
		closed = imageClosed;
		this.bugImage = new ImageView(open);
	}
	
	protected void updateBugImage() {
		imageRefresh += 1; // add 1 to imageRefresh each turn
		Image currentImage = bugImage.getImage(); // get bugs current displayed image
		if (currentImage == open && imageRefresh > 5) { // check if image is currently open and if imageRefresh has hit 20 turns.
			bugImage.setImage(closed); // swap image
			imageRefresh = 0; // reset imageRefresh
		}
		else if (currentImage == closed && imageRefresh > 5) { // check if image is currently open and if imageRefresh has hit 10 turns.
			bugImage.setImage(open); // swap image
			imageRefresh = 0; // reset imageRefresh
		}
	}
	
	public void getDestination(List<Bug> bugList, List<Plant> plantList, double width, double height) {
		/* getDestination method for Butterflies*/
//		ageUp(); // increase Bug age by 1
		this.hunger -= 0.5; // Decrease hunger every turn
		if (!isResting) this.restMeter += 0.5; // Increase restMeter by 1 every turn.
		this.updateBugImage(); // Updates Bug sprite for animation
		
		/* Bug b is the closest pray option. Will return null if no bugs in range. */
		Bug b = this.getBestPrey(bugList);
		
		/* Plant p is destination Plant, getBestPlant will return null if no Plants in sightRange.
		 * If Bug was eating last turn and no Plants in range, then Plant size hit 0 and was removed. */
		Plant p = this.getRestPlant(plantList);
		
		/* Boolean actionMade get turned true if Bug makes sneak attack on a bug or sets isEating to false. 
		 * Used to decide what actions to trigger later. */
		boolean actionMade = false;
		/* The next 3 if statements all require Bug b to be present. *
		 * These if statements use boolean triggers to track which action a Wasp will take. */
		if (b != null) {
			/* Check if Bug is hunting.
			 * isHunting will be true if Bug is chasing a butterfly and false if eating or seeking rest. */
			if (isHunting) {
				/* Check if a Bug is currently on a destination Bug (b).
				 * Bug cannot be null for method bugOnPrey */
				if (bugOnPrey(b)) { // if Bug is currently on destination Bug (b) run method eatPrey and set isHunting = false.
					if(eatPrey(b)) { // eatPrey will return true and set isEating to true if Bug successfully eats.
						isHunting = false; // set isHunting to false after capturing prey
						isEating = true; // set isEating to true if eatBug returns true.
					}
				}
				/* else if bugOnPrey returns false Bug will move towards Destination Bug (b). */
				else {
					hunt(b);
				}
			}
			
			/* Check if bug was eating with boolean isEating & that destination Bug (b) is not null
			 * If Bug was eating last turn and no Bug in range, then Bug hunger hit 0 and was removed.
			 * isEating will be true if a Bug used method eatPrey last turn and Bugs hunger < 700 || pref is devoured. */
			else if (isEating) {
				if(eatPrey(b)) {
					isEating = true; // set isEating to true if eatBug returns true.
				}
				else {
					isEating = false; // set isEating to false if Bug does not Eat
					actionMade = true; // set isEating false so need to track a turn made.
				}
			}
			
			/* If Wasp is currently resting and Butterfly approaches Wasp can hunt butterfly
			 * without triggering isHunting so Butterfly does not run away. */
			else if (isResting && hunger < hungerMax*0.6 && preyTooClose(b)) {
				actionMade = true; // in this scenario Bug will could a turn with resting, eating and hunting all false.
				if (bugOnPrey(b)) { // if Bug is currently on destination Bug (b) run method eatPrey and set isHunting = false.
					if(eatPrey(b)) { // eatPrey will return true and set isEating to true if Bug successfully eats.
						isResting = false; // Update isResting last to stop triggering this branch of if statement. 
						isEating = true; // set isEating to true if eatPrey returns true.
					}
				}
				/* else if bugOnPrey returns false Bug will move towards Destination Bug (b).
				 * is Hunting is not triggered here so Bug can make a surprise attack. */
				else {
					hunt(b);
				}
			}
		}
		
		/* The next 2 if statements both require Plant p to be present. *
		 * These if statements use boolean triggers to track which action a Wasp will take.
		 * If any of these booleans return true Wasp has already made a move this turn. */
		if (p != null && !actionMade && !isEating && ! isHunting) {
			/* Check if bug is now resting and not starving.
			 * Bug should seek food if it is starving. 
			 * Bug is starving if hunger is less than 30% */
			if (isResting && hunger > hungerMax*0.3) {
				/* Check if a Bug is currently on a destination Plant (p). */
				if (bugOnPlant(p)) { // if Bug is currently on destination Plant (p) run method rest
					if(!rest(p)) { // if rest returns false Bug is finished resting.
						isResting = false; // reset isResting.
						actionMade = true; // set isResting false so need to track a turn made.
					}
				}
				else moveTowardsPlant(p); // Move towards Plant to rest on
			}
			
			/* Check if Bug needs to rest. Bug needs to rest if restMeter is 500 or more. or if bug is currently resting. */
			else if (restMeter >= 500  && hunger > hungerMax*0.7) {
				/* If Wasp is on plant set isResting to true. */
				if (bugOnPlant(p)) isResting = true;
				/* Else move towards Destination Plant (p). */
				else {
					moveTowardsPlant(p);
					actionMade = true; // Need to track a turn made.
				}
			}
		}
		
		/* Last if to run is both b and p are both null. if checks will confirm that an action has not been made by Wasp this turn. */
		if (!actionMade  && !isEating && ! isHunting && !isResting) {
			/* Check if a Bug is currently hungry, Bug is hungry if double hunger < 500.
			 * Bug also needs confirmation of Bugs in sight range for method so Bug b cannot be null.
			 * Wasp will hunt and set isHunting = true. */
			if (hunger < hungerMax*0.8 && b != null) {
				hunt(b);
				isHunting = true;
			}
			/* If Bug had no destination Plant then Bug will keepMoving. */
			else keepMoving(width, height);
		}
		
		/* Fail safe checks in situation where Bug or Plant is null but Wasp expects otherwise. */
		if (isResting && p == null) isResting = false;
		if (isHunting && b == null) isHunting = false;
		if (isEating && p == null) isEating = false; 
	}
		
	protected boolean bugOnPrey(Bug b) {
		if (Math.abs(this.x - b.getX()) < b.getBugImageSize() && Math.abs(this.y - b.getY()) < b.getBugImageSize()) return true;
		return false;
	}
	
	/* Pretty much the same as above method but has +60 buffer to range. */
	private boolean preyTooClose(Bug b) {
		if (Math.abs(this.x - b.getX()) < b.getBugImageSize()+40 && Math.abs(this.y - b.getY()) < b.getBugImageSize()+40) return true;
		return false;
	}
	
	protected void hunt(Bug b) {
		/* Wasp gets increased speed by adding to dx and dy.
		 * Wasp loses extra hunger when hunting. */
		hunger -= 2; // hunting decreases hunger.
		/* Determine the direction of a Butterfly and set Bug x and y to move towards it. */
		double xDest = b.getX();
		double yDest = b.getY();
		/* Add speed to dx and dy. */
		double sdx = dx; 
		double sdy = dy;

		sdx =  sdx*3;
		sdx = sdy*3;
		
		/* if statements to confirm dx and dy are in correct direction
		 * updates direction if wrong. */
		if ((this.x > xDest && sdx > 0) || (this.x < xDest && sdx < 0)) sdx = -sdx;
		if ((this.y > yDest && sdy > 0) || (this.y < yDest && sdy < 0)) sdy = -sdy;
		
		/*set final move for bug and confirm if bug is directly N or S.
		 * If prey is N or S only update y. */
		if (Math.abs(this.x) >= Math.abs(xDest-20) && Math.abs(this.x) <= Math.abs(xDest+20)) {
			this.y += sdy*1.6; // if Wasp already has good x dont change it and boost y move
		}
		else {
			this.x += sdx; 
			this.y += sdy;
		}
		
	}
	
	protected Plant getRestPlant(List<Plant> plantList) {
		// decide which Butterfly is closest to Bugs current x and y coordinates
		Plant bestPlant = null;
		for (Plant p : plantList) {
			if (this.plantInRange(p) && p.getType().equals("Flower")) {
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
	
	protected Bug getBestPrey(List<Bug> bugList) {
		// decide which Butterfly is closest to Bugs current x and y coordinates
		Bug bestPrey = null;
		for (Bug b : bugList) {
			// if b is in sightRange of wasp and if b is a Butterfly.
			if (this.preyInRange(b) && b.getSpecies().equals("Butterfly")) {
				if (bestPrey == null) bestPrey = b;
				// get difference for each coordinate from Bug b
				double xdiff = this.x - b.getX();
				if (xdiff < 0) xdiff = -xdiff; // convert to positive number if it was negative
				double ydiff = this.y - b.getY();
				if (ydiff < 0) ydiff = -ydiff; // convert to positive number if it was negative
				// repeat and get difference for each coordinate from bestPrey
				double BPxdiff = this.x - bestPrey.getX();
				if (BPxdiff < 0) BPxdiff = -BPxdiff; // convert to positive number if it was negative
				double BPydiff = this.y - bestPrey.getY();
				if (BPydiff < 0) BPydiff = -BPydiff; // convert to positive number if it was negative
				// decide which is overall closer and make sure bug hunger is > 0
				if(((xdiff+ydiff) < (BPxdiff+BPydiff)) && b.getHunger() > 0) bestPrey = b;
				}
			}
			return bestPrey;
		}
		
		protected boolean preyInRange(Bug b) {
			//check if T is within a bugs sightRange
			if (this.x - b.getX() < sightRange && this.x - b.getX() > -sightRange && 
					this.y - b.getY() < sightRange && this.y - b.getY() > -sightRange) {
				return true;
			}
		return false;
		}
		
		protected boolean eatPrey(Bug b) {
			/* Method for a Wasp 'eating a butterfly'. Return true if Bug eats, False if Bug is full or cannot eat Bug.
			 * if a Bug is full (hunger = 700) Bug no longer needs to eat and boolean isEating will changed to false.
			 * else if Bug is still hungry Bug will attempt to eat Butterfly, if Butterfly hunger hits 0 method will return false. */
			if (hunger >= hungerMax) { // check if bug is 'full'.
				b.setIsPrey(false); // Bug is no longer pray.
				restMeter = 500; // Set 'restMeter' to 500 so bug immediately goes to rest after getting full.
				return false; // Bug did not 'eat', return false.
			}
			else { 
				if (b.getHunger() > 0) { // if preys hunger > 0 Butterfly can be 'eaten'.
					hunger += 10; // increase Bug hunger
					restMeter += 5; // increase restMeter for full belly.
					double preyHP = b.hunger - 35; // preys hunger will be hunger - 10.
					b.setHunger(preyHP); // set preys new hunger
					b.setIsPrey(true);
					return true; // return true as Bug did eat.
				}
				else { // if preys hunger is 0 Butterfly is 'eaten'.
					b.setIsPrey(false);
					/* If Bug died and Wasp still urgently needs food set 'restMeter' to 0 so Wasp does not rest before seeking food. */
					if (hunger < hunger*0.3) restMeter = 0; 
					return false; // Bug did not 'eat', return false.
				}
			}
		}
		
		protected boolean bugOnPlant(Plant p) {
			// (p.getSize()*0.4) represents current size of Plant drawn in animation.
			if (Math.abs(this.x - p.getX()) < (p.getSize()*0.4) && Math.abs(this.y - p.getY()) < (p.getSize()*0.4)) return true;
			return false;
		}
		
		protected boolean rest(Plant p) {
			/* If Wasp is resting hunger gets refunded and restMeter gets decreased. */
			hunger += 1.2;
			restMeter -= 5;
			if (restMeter <= 0) {
				return false;
			}
			return true;
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
		
		protected void keepMoving(double width, double height) {
			/* Bug will continue moving until interrupted. */
			// Check for edges of map and update direction (dx, dy) if Bug hits edge of map.
			if (this.x < bugImageSize || this.x + bugImageSize > width) dx = -dx;
			if (this.y <= bugImageSize || this.y + bugImageSize > height) dy = -dy;
			
			// Adjust x and y by final direction
			this.x += dx;
			this.y += dy;
		}

		public boolean getIsHunting() {
			return isHunting;
		}

		

		
		
}
