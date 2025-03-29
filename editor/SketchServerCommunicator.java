import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2025
 * @author Richmond Nartey Kwalah Tettey CS10, Winter 2025
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");

			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world

			//for each shape in server's map, send to client
			for(int id: server.getSketch().getSketches().keySet()){
				send("add " + server.getSketch().getSketches().get(id).toString() + " " + id);
			}

			// Keep getting and handling messages from the client

			String message;
			// Continuously read messages from the input stream
			while((message = in.readLine()) != null){

				// Extract the command from the received message
				String command = MessageProcessor.getCommand(message);

				// Extract the ID associated with the message
				int id = MessageProcessor.getId(message);

				// Declare a new shape object to be processed
				Shape newShape = null;
				try {
					// Process the message to create a new shape object
					newShape = MessageProcessor.processMessage(MessageProcessor.getProperty(message));
				}catch(Exception e){
					// Send an empty response if an error occurs
					send(" ");
				}

				// Handle the "delete" command
				if (command.equals("delete")){
					// Remove the shape with the specified ID from the sketch
					server.getSketch().getSketches().remove(id);
					// Print deletion confirmation
					System.out.println( id + " deleted successfully");
					// Broadcast the deletion message to all clients
					server.broadcast(message);

					// Handle the "add" command
				}else if(command.equals("add")){

					// Generate a new unique ID for the shape
					int shapeID = server.getNewID();

					// Add the new shape with the generated ID to the server's sketch object
					server.getSketch().getSketches().put(shapeID,newShape);

					// Replace the default ID in the message with the new ID
					message = message.substring(0, message.lastIndexOf(" "));
					message += " " + shapeID;

					// Broadcast the updated message to all clients
					server.broadcast(message);

					// Print confirmation of the added shape
					System.out.println("(id) " + shapeID + " someone successfully added " + newShape.getName());

					// Handle the "recolor" and "move" commands
				}else if(command.equals("recolor") || command.equals("move")){

					// If the specified ID does not exist in the sketch, send an empty response and return
					if(!server.getSketch().getSketches().containsKey(id)) {send(" ");return;}

					// Update the sketch with the modified shape
					server.getSketch().getSketches().put(id,newShape);
					// Broadcast the change to all clients
					server.broadcast(message);

					// Determine the message to be sent based on action
					String info = command.equals("recolor") ? "recolored" : "moved";

					// Print confirmation of the change
					System.out.println("(id) " + id + " someone successfully " + info + " " + newShape.getName());

				}else {
					// Send an empty response for unrecognized commands and return
					send(" "); return;
				}

			}


			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
