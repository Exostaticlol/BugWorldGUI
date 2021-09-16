import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Plant {

	private double x = 0, y = 0;
	private int size = 50;
	ImageView plantImage;
//	private double bugImageSize = 40; // I made the image 40x40 
	
	
	public Plant(double x, double y) {
		this.x = x;
		this.y = y;
		this.setPlantImage();
	}
	
	public void setPlantImage() { 
		/* I currently only have 1 bug, 
		* derived classes will override this method. */
		String imageUrl = "/images/Shrub1.png";
		Image image = new Image(imageUrl);
		this.plantImage = new ImageView(image);
		// update to get size and update size later in life
	}
	
	public void eatPlant(List<Plant> plantList) {
		this.size -= 5;
	}
	
	public void growPlant() {
		if (size < 50) this.size += 1;
	}
	
	public ImageView getPlantImage() {
		return this.plantImage;
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
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	
}
