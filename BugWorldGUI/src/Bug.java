
import java.util.List;
import javafx.scene.image.ImageView;

import javafx.scene.image.Image;

public class Bug {
	
	private double x, y, dx, dy;
	private double sightRange;
	private double hunger = 300;
	protected String species, diet;
	public ImageView bugImage;
	private double bugImageSize = 20; // I made the image 20x20 
	
	// World = world;
	
	public Bug(String species, String diet, 
			double x, double y, double speed, double sightRange) {
		this.species = species;
		this.diet = diet;
		this.x = x;
		this.y = y;
		this.dx = speed;
		this.dy = speed;
		this.sightRange = sightRange;
		this.setBugImage(); // set image for the bug
		
	}
	
	public void setBugImage() { 
		/* I currently only have 1 bug, 
		* derived classes will override this method. */
		String imageUrl = "/images/butterfly lol.png";
		Image image = new Image(imageUrl);
		this.bugImage = new ImageView(image);
		// update to get size and update size later in life
	}
	
	public void getDestination(List<Bug> bugList, List<Plant> plantList, double width, double height) {
		this.hunger -= 1;
		if (hunger < 250 && this.foodNear(plantList)) { // if Bug is hungry and there is food source within sightRange.
			Plant p = this.getBestPlant(plantList); // p is destination Plant
			if (bugOnPlant(p) && hunger < 90) {
				this.hunger += 5;
				p.eatPlant(plantList);
			}
			else {
				Double xDest = p.getX();
				Double yDest = p.getY();
				// if statements to confirm dx and dy are in correct direction
				// updates direction if wrong
				if ((this.x > xDest && dx > 0) || (this.x < xDest && dx < 0)) dx = -dx;
				if ((this.y > yDest && dy > 0) || (this.y < yDest && dy < 0)) dy = -dy;
				
				//get final direction
				double newX = this.x += dx;
				double newY = this.y += dy;
				//only update x and y is bug not already on Plant
				if (newX - xDest > 20) this.x = newX;
				if (newY - yDest > 20) this.y = newY;
			}
		}
		else { // if not hungry move same direction until interrupted
			if (this.x < bugImageSize || this.x + bugImageSize > width) dx = -dx;
			if (this.y <= bugImageSize || this.y + bugImageSize > height) dy = -dy;
			
			//get final direction
			double newX = this.x += dx;
			double newY = this.y += dy;
			//check that there is no bug already in target location
			this.x = newX;
			this.y = newY;
		}
	}

	private boolean bugOnPlant(Plant p) {
		if (Math.abs(this.x - p.getX()) < 30 && Math.abs(this.y - p.getY()) < 30) return true;
		return false;
	}

	// runs for each on plantList returns true if a plant is detected in range
	private boolean foodNear(List<Plant> plantList) {
		for (Plant p : plantList) {
			//check if plant is within a bugs sightRange
			if (this.x - p.getX() < sightRange && this.x - p.getX() > -sightRange && 
					this.y - p.getY() < sightRange && this.y - p.getY() > -sightRange) {
				return true;
			}
		}
		return false;
	}

	// decide which Plant is closest to Bugs current x and y coordinates
	private Plant getBestPlant(List<Plant> plantList) {
		Plant bestPlant = null;
		for (Plant p : plantList) {
			if (this.plantInRange(p) && p.getSize() > 1) {
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
	
	private boolean plantInRange(Plant p) {
		//check if plant is within a bugs sightRange
		if (this.x - p.getX() < sightRange && this.x - p.getX() > -sightRange && 
				this.y - p.getY() < sightRange && this.y - p.getY() > -sightRange) {
			return true;
		}
	return false;
	}

	public ImageView getBugImage() {
		return bugImage;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getDiet() {
		return diet;
	}

	public void setDiet(String diet) {
		this.diet = diet;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}
	
	public double getHunger() {
		return hunger;
	}
	
}
