import java.awt.Color;
import java.awt.Graphics;

/**
 * A rectangle-shaped Shape
 * Defined by an upper-left corner (x1,y1) and a lower-right corner (x2,y2)
 * with x1<=x2 and y1<=y2
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, updated Fall 2016
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2025
 * @author Richmond Nartey Kwalah Tettey CS10, Winter 2025
 */
public class Rectangle implements Shape {
	private int x1, y1,x2,y2; // upper left and bottom right
	private Color color;

	/**
	 * an "empty" rectangle with only one point set so far
	 * */
	public Rectangle(int x1, int y1, Color color){
		this.x1 = x1; this.y1 = y1; this.color = color;
	}

	/**
	 * rectangle with two points
	 * */
	public Rectangle(int x1, int y1, int x2, int y2, Color color){
		this.x1 = x1; this.y1 = y1;
		this.x2 = x2; this.y2 = y2;
		this.color = color;
	}

	/**
	 * @return name of shape
	 * */
	public String getName(){
		return "rectangle";
	}

	/**
	 * redefines the rectangle based on new corners
	 * */
	public void setCorners(int x1, int y1, int x2, int y2){
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1,y2);
		this.x2 = Math.max(x1,x2);
		this.y2 = Math.max(y1,y2);
	}

	/**
	 * updates points of rectangle to new location
	 * */
	@Override
	public void moveBy(int dx, int dy) {
		x1 += dx; y1 += dy;
		x2 += dx; y2 += dy;
	}

	/**
	 * getter and setter for color
	 * */
	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * set new color of shape
	 * @param color is the new color object
	 * */
	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @param x is the x-coordinate of mouse pointer
	 * @param y is the y-coordinate of mouse pointer
	 * @return true if x and y points is within rectangle
	 * */
	@Override
	public boolean contains(int x, int y) {
		return (x <= x2 && x >= x1 && y >= y1 && y <= y2);
	}

	@Override
	public void draw(Graphics g) {

		//draw new rectangle
		g.setColor(color);
		g.fillRect(x1,y1, x2-x1, y2-y1);
	}

	/**
	 * returns type of shape, coordinates and color
	 * */
	@Override
	public String toString() {
		return "rectangle "+x1+" "+y1+" "+x2+" "+y2+" "+color.getRGB();
	}
}
