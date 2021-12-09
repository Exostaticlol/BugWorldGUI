import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class World {
	
	public int width;
	public int height;
	
	/* Very important Lists. */
	private List<Bug> bugList = new ArrayList<Bug>(); // Hold bugs in the world
	private List<Plant> plantList = new ArrayList<Plant>(); // Hold plants in the world
	
	public World(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/* Used with stop button.
	 * Clear all Bug & Plants from Lists. */
	public void resetWorld() {
		bugList.clear();
		plantList.clear();
	}
	
	public List<Bug> addBugs(String type, int n) {
		/* create n amount of Bugs with random x and y coordinates.
		 * currently only bug is Butterfly. */
		List<Bug> newBugs = new ArrayList<Bug>();
		while (newBugs.size() < n) { 
			double x = getRandXY("x");
			while (x < 40 || x > width-40) {
				x = getRandXY("x");
			}
			double y = getRandXY("y");
			while (y < 40 || y > width-40) {
				y = getRandXY("y");
			}
			//type determines type of Bug and add new bug to List newList
			if (type.equals("Butterfly")) newBugs.add(new Butterfly(x, y));
			if (type.equals("Caterpillar")) newBugs.add(new Caterpillar(x, y));
			if (type.equals("Wasp")) newBugs.add(new Wasp(x, y));
		}
		bugList.addAll(newBugs); // Update main bugList with new Bugs.
		return newBugs; 
	}
	
	public List<Plant> addPlants(String type, int n) {
		/* create n amount of Bugs with random x and y coordinates.
		 * currently only bug is butterfly only diet option is Plant. */
		List<Plant> newPlants = new ArrayList<Plant>();
		while (newPlants.size() < n) { 
			double x = getRandXY("x");
			while (x < 40 || x > width-40) {
				x = getRandXY("x");
			}
			double y = getRandXY("y");
			while (y < 40 || y > width-40) {
				y = getRandXY("y");
			}
			//type determines type of Bug and add new bug to List newList
			if (type.equals("Shrub")) newPlants.add(new Shrub(x, y));
			if (type.equals("Flower")) newPlants.add(new Flower(x, y));
		}
		plantList.addAll(newPlants); // Update main plantList with new Plants.
		return newPlants;
	}
	
	private double getRandXY(String XY) {
		// Returns a random x or y used for creating Bugs and Plants
		if (XY.equals("x")) {
			double randomX = Math.floor(Math.random() * this.width-40);
			return randomX;
		}
		else { // if XY = "y".
			double randomY = Math.floor(Math.random() * this.height-40);
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
