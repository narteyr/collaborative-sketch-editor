# Collaborative Sketch Editor

A simple client-server graphical editor that allows multiple users to collaboratively draw, move, recolor, and delete shapes in real-time. The project is built using Java Swing for the GUI and an Echo Server for communication.

## Features
- **Drawing Mode**: Supports ellipse, rectangle, freehand, and line segment drawing.
- **Move Mode**: Allows users to reposition existing shapes.
- **Recolor Mode**: Enables changing the color of selected shapes.
- **Delete Mode**: Removes unwanted shapes from the canvas.
- **Real-time Collaboration**: Multiple clients can connect and modify the same sketch.

## Project Structure
Collaborative-Sketch-Editor/
│── src/
│   ├── Editor.java          # Main client-side graphical editor
│   ├── Shape.java           # Base class for drawable shapes
│   ├── Ellipse.java         # Ellipse shape implementation
│   ├── Rectangle.java       # Rectangle shape implementation
│   ├── Segment.java         # Line segment shape implementation
│   ├── Polyline.java        # Freehand drawing shape implementation
│   ├── Sketch.java          # Manages the collection of drawn shapes
│   ├── EditorCommunicator.java # Handles client-server communication
│   ├── EchoServer.java      # Simple echo server to handle multiple clients
│── README.md                # Project documentation
│── .gitignore               # Git ignored files
│── run.sh                   # Shell script to run the server and 


## Prerequisites
- Java Development Kit (JDK) 8 or later
- A terminal or command prompt

## Running the Project

### 1. Start the Server
Before launching the editor, you must start the Echo Server to handle client connections.

## Initiate Project
- javac EchoServer.java
- java EchoServer <port>

- javac Editor.java
- java Editor <serverIP> <port>

## Connect Multiple Users
	•	Each user should connect using java Editor <serverIP> <port>.
	•	All connected users will see and interact with the same sketch.

## Future Improvements
	•	Implement a more efficient networking protocol.
	•	Add an undo/redo functionality.
	•	Improve user authentication and access control.

## Contributors
	•	Richmond Nartey Kwablah Tettey
	•	Dartmouth CS 10, Winter 2025

## License
This project is released under the MIT License.