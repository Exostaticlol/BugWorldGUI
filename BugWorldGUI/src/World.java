import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class World {
	
	public int width;
	public int height;
	
	private List<Bug> bugList = new ArrayList<Bug>();
	private List<Plant> plantList = new ArrayList<Plant>();
	
	//maybe a hash map, coord key and value is bug or plant?
	
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		/* quick method to create and add bugs to bugList
		 * int 20 is number of bugs to create. */
		this.addBugs(10); 
		this.addPlants(4);
	}
	
	private void addBugs(int n) {
		/* create n amount of Bugs with random x and y coordinates.
		 * currently only bug is butterfly only diet option is Plant. */
		while (bugList.size() < n) { 
			double x = getRandXY("x");
			double y = getRandXY("y");
			
			//newBug(species, diet, x, y, speed, sightRange)
			Bug b = new Bug("Butterfly", "Plant", x, y, 1.5, 75);
			// add new bug to List. Key = String "x,y", Bug b
			bugList.add(b); 
		}
	}
	
	private void addPlants(int n) {
		/* create n amount of Bugs with random x and y coordinates.
		 * currently only bug is butterfly only diet option is Plant. */
		while (plantList.size() < n) { 
			double x = getRandXY("x");
			double y = getRandXY("y");
			
			//newBug(species, diet, x, y, speed, sightRange)
			Plant p = new Plant(x, y);
			// add new bug to List. Key = String "x,y", Bug b
			plantList.add(p); 
		}
	}
	
	private double getRandXY(String XY) {
		if (XY.equals("x")) {
			double randomX = Math.floor(Math.random() * this.width);
			return randomX;
		}
		else { // if XY = "y".
			double randomY = Math.floor(Math.random() * this.height);
			return randomY;
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<Bug> getBugList() {
		return bugList;
	}

	public List<Plant> getPlantList() {
		return plantList;
	}
	
	public void setBugList(List<Bug> newBugList) {
		bugList.clear();
		bugList.addAll(newBugList);
	}
	
	public void setPlantList(List<Plant> newPlantList) {
		plantList.clear();
		plantList.addAll(newPlantList);
	}
}
