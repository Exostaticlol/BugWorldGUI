import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Flower extends Plant {
	
	private double mX, mY; 

	public Flower(double x, double y) {
		super("Flower", x, y, 20);
		mX = x;
		mY = y;
	}
	
	/* get a random int from 1-5 and use int with string to get flower image.
	 * 'flower1.png ... ' */
	protected void setPlantImage() { 
		Random rand = new Random();
		int n = rand.nextInt(5) + 1;
		String imageUrl = "/images/flower" + n + ".png";
		Image image = new Image(imageUrl);
		this.plantImage = new ImageView(image);
	}

	public boolean eatPlant() {
		if (size-0.5 < 0) return false;
		size -= 0.5;
		super.x = mX + ((20 - (size/2.5))/(size/2.5));
		super.y = mY + ((20 - (size/2.5))/(size/2.5));
		return true;
	}
	
	public void growPlant() {
		if (size < 20 && size > 0) {
			this.size += 2; // if size hit 0 plant will not grow so it can be removed from plantList in GUI
			super.x = mX + ((20 + (size/2.5))/(size/2.5));
			super.y = mY + ((20 + (size/2.5))/(size/2.5));
			
		}
		if (size >= 20) {
			size = 20;
			super.x = mX;
			super.y = mY;
		}
	}
	
	public double getSize() {
		return super.size;
	}
}
