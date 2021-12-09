import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Shrub extends Plant {
	
	private double mX, mY; 
	
	public Shrub(double x, double y) {
		super("Shrub", x, y, 100);
		mX = x;
		mY = y;
	}
	
	protected void setPlantImage() { 
		String imageUrl = "/images/Shrub1.png";
		Image image = new Image(imageUrl);
		this.plantImage = new ImageView(image);
	}
	
	public boolean eatPlant() {
		if (size < 0) return false;
		size -= 1;
		super.x = mX + ((100 - (size/2.5))/(size/2.5));
		super.y = mY + ((100 - (size/2.5))/(size/2.5));
		return true;
	}
	
	public void growPlant() {
		if (size < 100 && size > 0) {
			this.size += 1; // if size hit 0 plant will not grow so it can be removed from plantList in GUI
			super.x = mX + ((100 + (size/2.5))/(size/2.5));
			super.y = mY + ((100 + (size/2.5))/(size/2.5));
			
		}
		if (size >= 100) {
			size = 100;
			super.x = mX;
			super.y = mY;
		}
	}
	
	public double getSize() {
		return super.size/2.5;
	}

}
