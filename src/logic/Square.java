package logic;

import java.io.Serializable;

public class Square implements Serializable {
	public int color;
	public int x;
	public int y;
	public int size = 0;
	
	public transient int maxSize;
	public transient boolean growing = true;
	private transient int growthSpeed = 1;
	private transient boolean completed = false;
	
	public Square(int _x, int _y, int _maxSize) {
		x = _x;
		y = _y;
		maxSize = _maxSize;
		createRandomColor();
	}
	
	private void createRandomColor() {
		int red = (int)(256 * Math.random());
		int green = (int)(256 * Math.random());
		int blue = (int)(256 * Math.random());
		
		System.out.println("red: " + red);
		System.out.println("green: " + green);
		System.out.println("blue: " + blue);
		
		color = (red << 24) + (green << 16) + (blue << 8);
	}
}
