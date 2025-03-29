import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2025
 * @author Richmond Nartey Kwalah Tettey CS10, Winter 2025
 */
public class Polyline implements Shape {
	private List<Integer> points; //contains all poitns of polyline
	private Color color; //color of polyline

	//constructor method
	public Polyline(int x, int y, Color color){
		points = new ArrayList<>();
		points.add(x);
		points.add(y);
		this.color = color;
	}

	//constructor for polyline
	public Polyline(Color color){
		points = new ArrayList<>();
		this.color = color;
	}

	public String getName(){
		return "freehand";
	}

	//move polyline
	@Override
	public void moveBy(int dx, int dy) {

		for(int i = 0; i < points.size() / 2; i++){

			points.set(i * 2, points.get(i*2) + dx);
			points.set(i * 2 + 1, points.get(i *2 +1) + dy);
		}
	}

	/**
	 * @return color of shape
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
	 * add a new point to point list
	 * @param x is the x-coordinate
	 * @param y is the y-coordinate
	 * */
	public void addPoint(int x, int y){
		points.add(x); points.add(y);
	}

	/**
	 * @param x is the x-coordinate to compare with segment
	 * @param y is the y-coordinate to compare with the segment
	 * @return true if coordinate is close enough to line, or else otherwise
	 * */
	@Override
	public boolean contains(int x, int y) {

		for(int i = 0; i < (points.size() / 2) - 1; i++){

			//compute distance between point and closest segment/polyline
			Double value = Segment.pointToSegmentDistance(x,y,
					points.get(i * 2), points.get(i * 2 + 1),
					points.get(i * 2 + 2), points.get(i * 2 + 3));


			//if distance is close return true
			if (value <= 3){
				return true;
			}
		}

		return false;
	}

	/**
	 * draw multiple segments for polyline
	 * @param g is the graphics object of Jframe
	 * */
	@Override
	public void draw(Graphics g) {

		//set graphics color to current
		g.setColor(color);


		//iterate point list
		for(int i = 0; i < (points.size() / 2) - 1; i++){

			//draws segments between two points
			g.drawLine(points.get(i * 2), points.get(i * 2 + 1),
					points.get(i * 2 + 2), points.get(i * 2 + 3));
		}


	}


	@Override
	public String toString() {
		String points = "";

		for (int i = 0; i < this.points.size() - 1;i++){
			points += this.points.get(i) + " ";
		}

		points += this.points.getLast();

		//return properties of shape
		return "polyline" + " " + points + " " +color.getRGB();
	}
}
