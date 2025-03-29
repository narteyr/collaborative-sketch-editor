import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Client-server graphical editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2025
 * @author Richmond Nartey Kwalah Tettey CS10, Winter 2025
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged

	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};
		
		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});
		
		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {

		// Iterate through all shapes in the sketch, retrieving them by their IDs
		for (int id : sketch.getSketches().navigableKeySet()) {
			// Get the shape associated with the current ID
			Shape shape = sketch.getSketches().get(id);
			// Draw the shape on the Graphics context
			shape.draw(g);
		}

		// Draw the currently selected shape if it exists
		if (curr != null) {
			curr.draw(g);
		}
	}

	// Helpers for event handlers
	
	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {

		// If the current mode is draw, create a new shape
		if (mode == Mode.DRAW) {
			// Draw a new shape and set drawFrom

			if (shapeType.equals("ellipse")) {
				curr = new Ellipse(p.x, p.y, color);
				drawFrom = p; // Store the starting point
			} else if (shapeType.equals("rectangle")) {
				curr = new Rectangle(p.x, p.y, color);
				drawFrom = p; // Store the starting point
			} else if (shapeType.equals("freehand")) {
				curr = new Polyline(p.x, p.y, color); // Create a freehand polyline
			} else if (shapeType.equals("segment")) {
				curr = new Segment(p.x, p.y, color); // Create a segment
			}

		}
		else if (mode == Mode.MOVE) {
			// If in move mode, check if the click is inside a shape

			for (int id : sketch.getSketches().keySet()) {
				Shape shape = sketch.getSketches().get(id);

				if (shape.contains(p.x, p.y)) { // Check if point is inside the shape
					moveFrom = p; // Store the point where movement starts
					curr = shape; // Store the selected shape
					movingId = id; // Store the ID of the shape being moved
				}
			}

		}
		else if (mode == Mode.RECOLOR) {
			// If in recolor mode, change the color of the clicked shape

			for (int i : sketch.getSketches().keySet()) {
				Shape shape = sketch.getSketches().get(i);

				if (shape.contains(p.x, p.y)) { // Check if point is inside the shape
					curr = shape; // Store the selected shape
					movingId = i; // Store the ID of the shape being recolored
					curr.setColor(color); // Change the color of the shape

					System.out.println("current shape:" + curr); // Print the modified shape
				}
			}

		}
		else if (mode == Mode.DELETE) {
			// If in delete mode, check if the click is inside a shape

			for (int i : sketch.getSketches().keySet()) {
				Shape shape = sketch.getSketches().get(i);

				if (shape.contains(p.x, p.y)) { // Check if point is inside the shape
					movingId = i; // Store the ID of the shape to be deleted
					curr = shape; // Store the selected shape
				}
			}
		}

		repaint(); // Refresh the UI to reflect changes
	}

	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {

		//revise the shape as it is stretching out
		if(mode == Mode.DRAW){

			if(shapeType.equals("rectangle")){
				((Rectangle)curr).setCorners(drawFrom.x,drawFrom.y,p.x,p.y);
			}else if (shapeType.equals("ellipse")){
				((Ellipse) curr).setCorners(drawFrom.x, drawFrom.y,p.x,p.y);
			} else if(shapeType.equals("freehand")){
				((Polyline) curr).addPoint(p.x,p.y);
			}else if (shapeType.equals("segment")){
				((Segment) curr).setEnd(p.x,p.y);
			}

			//moving mode -> shift the object and keep track of where next step is from
		} else if(mode == Mode.MOVE){
			if(moveFrom != null){
				curr.moveBy(p.x - moveFrom.x, p.y - moveFrom.y);
				moveFrom = p;
			}
		}

		//make changes to editor
		repaint();
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it		
	 */
	private void handleRelease() {

		if (curr != null) {
			// Check the current mode and send the appropriate request
			if (mode == Mode.DRAW) {
				System.out.println("request to draw new shape");
				// Send a request to add a new shape with its properties and ID
				comm.request("add" + " " + curr.toString() + " " + movingId);
			} else if (mode == Mode.MOVE) {
				System.out.println("request to change shape position");
				// Send a request to move the selected shape
				comm.request("move" + " " + curr.toString() + " " + movingId);
			} else if (mode == Mode.RECOLOR) {
				System.out.println("request to recolor shape");
				// Send a request to recolor the selected shape
				comm.request("recolor" + " " + curr.toString() + " " + movingId);
			} else if (mode == Mode.DELETE) {
				System.out.println("request to delete shape");
				// Send a request to delete the selected shape
				comm.request("delete" + " " + curr.toString() + " " + movingId);
			}
		} else {
			// No shape has been selected, so print an error message
			System.out.println("no object selected");
		}

		//reset the instance variables to default
		moveFrom = null;
		drawFrom = null;
		curr = null;
		movingId = -1;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}
