import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Plant {

	protected String type;
	protected double x, y;
	protected double size;
	ImageView plantImage;
	
	
	public Plant(String type, double x, double y, double size) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.size = size;
		this.setPlantImage();
	}
	
	protected void setPlantImage() { 
		/* Derived classes must include override for this method. 
		 * This method is used to set ImageView plantImage.
		 * Images are in images package.
		 * ImageView is used in timeline to display Plants.*/
	}
	
	public boolean eatPlant() {
		/* Currently only Shrub overrides this so always check Plant type before calling. */
		return false;
	}
	
	public void growPlant() {
		/* Currently only Shrub overrides this so always check Plant type before calling. */
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
	
	public double getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
	
}
