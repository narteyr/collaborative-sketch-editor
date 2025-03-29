import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2025
 * @author Richmond Nartey Kwalah Tettey CS10, Winter 2025
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			//Socket sock = new Socket(serverIP, 4242);
			Socket sock = new Socket();
			sock.connect(new InetSocketAddress(serverIP, 4242), 2000);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages


			System.out.println("waiting for response");

            String message;
			// Continuously read messages from the input stream
			while((message = in.readLine()) != null){

				// Check if the received message is an empty space indicating failure to add a shape
				if (message.equals(" ")){
					System.out.println("server failed to add shape");
					return;
				}

				try {
					// Extract the command from the received message
					String command = MessageProcessor.getCommand(message);

					// Extract the properties of the shape from the message
					String properties = MessageProcessor.getProperty(message);

					// Extract the ID associated with the shape
					int id = MessageProcessor.getId(message);

					// Handle the "delete" command
					if (command.equals("delete")){
						// Remove the shape with the specified ID from the local sketch map
						editor.getSketch().getSketches().remove(id);
						System.out.println( id + " deleted successfully");
						// Repaint the editor to reflect changes
						editor.repaint();

					} else {
						// Process the message to create a new shape object
						Shape shape = MessageProcessor.processMessage(properties);
						System.out.println("new " + shape.getName() + " shape created");

						// Add the newly created shape to the sketch map
						editor.getSketch().addNewSketch(id,shape);
						System.out.println("new sketch added" + "\n");

						// Repaint the editor to display the new shape
						editor.repaint();
					}

				} catch (Exception e){
					// Print any exceptions that occur during processing
					System.out.println(e);
				}

			}

		}
		catch (IOException e) {
			// Handle any IO exceptions that may occur
			e.printStackTrace();
		}
		finally {
			// Print a message when the server disconnects
			System.out.println("server hung up");
		}
	}

	// Send editor requests to the server
    public void request(String msg){
        out.println(msg);
    }
}
