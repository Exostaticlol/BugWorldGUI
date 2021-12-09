
import java.util.List;
import javafx.scene.image.ImageView;

import javafx.scene.image.Image;

public class Bug {
	
	protected double x, y, dx, dy; // values used to determine a Bugs position on the map and move Bug.
	protected double sightRange; // How for from x and y a Bug and see
	protected double hunger; // Represents how full a bug is, decreases every turn and increases if bug 'eats'
	protected double hungerMax; // Max hunger level to determine if Bug has Max hunger
	protected int age = 0; // counter to track Bug age. Current max age for Bugs is 2500 defined in refreshWorldList in GUI
	protected String species, diet; // Holds species of Bug (derived classes name) and diet (Plant or another Bug species).
	protected Image open, closed; // 2 images for Bug to use in moving animation
	public ImageView bugImage; // actual ImageView of Bug on map
	protected int imageRefresh = 0; // a counter for refreshing a Bugs image
	protected double bugImageSize = 20; // I made all Bug images 20x20 
	protected boolean isEating = false; // Used in moving algorithms is true if Bug 'ate' last turn.
	
	public Bug(String species, String diet, 
			double x, double y, double speed, double hungerMax, double sightRange) {
		this.species = species;
		this.diet = diet;
		this.x = x;
		this.y = y;
		this.dx = speed;
		this.dy = speed;
		this.hunger = hungerMax;
		this.hungerMax = hungerMax;
		this.sightRange = sightRange;
		setBugImage(); // set image for the bug
	}
	
	
	protected void setBugImage() { 
		/* Derived classes must include override for this method. 
		 * This method is used to set image open, closed and ImageView bugImage.
		 * Images are in images package.
		 * open and closed are 2 different states for images so movement animation.
		 * ImageView is used in timeline to display Bugs.*/
	}
	
	
	protected void updateBugImage() {
		/* Derived classes must include override for this method.
		 * This method updates ImageView bugImage after int imageRefresh hits certain threshold. */
	}
	

	
	public void getDestination(List<Bug> bugList, List<Plant> plantList, double width, double height) {
		/* This method requires List of Bugs and List of Plants from World. 
		 * and width and height of World.
		 * Derived classes must override this method.*/
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
	
	public void setHunger(double hunger) {
		this.hunger = hunger;
	}


	public double getBugImageSize() {
		return bugImageSize;
	}
	
	public int getAge() {
		return age;
	}
	
	public void ageUp() {
		age += 1;
	}


	public void setIsPrey(Boolean prey) {
		// for butterfly and caterpillar
	}
	
}
