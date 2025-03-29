import java.awt.*;


/**
 * To convert shape properties to shape object
 * @author Richmond Nartey Kwalah tettey CS 10, Winter 2025
 * */

public class MessageProcessor {


    /**
     * Processes a message from the server and returns a new Shape object.
     * The message contains information about the shape type, its coordinates, and color.
     *
     * @param property The input message string, formatted as: "ShapeType x1 y1 x2 y2 color"
     * @return A new Shape object corresponding to the given message.
     * @throws Exception if the message format is incorrect or if the shape type is invalid.
     */
    public static Shape processMessage(String property) throws Exception {

        // Split the message to extract properties
        String[] properties = property.split(" ");
        String nameOfShape = properties[0]; // First element is the shape type
        String color = properties[properties.length - 1]; // Last element is the color

        // Convert color string to a Color object
        Color newColor = new Color(Integer.parseInt(color)); // Ensure color is a valid integer

        Shape shape = null; // Initialize shape to null

        // Process based on the shape type
        switch (nameOfShape) {

            case "ellipse": {
                // Check if the correct number of coordinates is provided (4 expected)
                if (properties.length - 2 != 4) {
                    throw new Exception("Expecting 4 coordinates for Ellipse");
                }

                // Create an Ellipse object
                shape = new Ellipse(
                        Integer.parseInt(properties[1]), // x1
                        Integer.parseInt(properties[2]), // y1
                        Integer.parseInt(properties[3]), // x2
                        Integer.parseInt(properties[4]), // y2
                        newColor
                );
                break; // Add break statement to prevent fall-through
            }

            case "rectangle": {
                // Check if the correct number of coordinates is provided
                if (properties.length - 2 != 4) {
                    throw new Exception("Expecting 4 coordinates for Rectangle");
                }

                // Create a Rectangle object
                shape = new Rectangle(
                        Integer.parseInt(properties[1]),
                        Integer.parseInt(properties[2]),
                        Integer.parseInt(properties[3]),
                        Integer.parseInt(properties[4]),
                        newColor
                );
                break; // Add break statement
            }

            case "segment": {
                // Check if the correct number of coordinates is provided
                if (properties.length - 2 != 4) {
                    throw new Exception("Expecting 4 coordinates for Segment");
                }

                // Create a Segment object
                shape = new Segment(
                        Integer.parseInt(properties[1]),
                        Integer.parseInt(properties[2]),
                        Integer.parseInt(properties[3]),
                        Integer.parseInt(properties[4]),
                        newColor
                );
                break; // Add break statement
            }

            case "polyline": {
                // Create a Polyline object
                shape = new Polyline(newColor);

                // Parse coordinates in pairs
                for (int i = 1; i < properties.length - 2; i += 2) {
                    ((Polyline) shape).addPoint(
                            Integer.parseInt(properties[i]),
                            Integer.parseInt(properties[i + 1])
                    );
                }
                break; // Add break statement
            }

            default:
                //throws exception if shape sent in message is not valid to the editor
                throw new Exception("Unknown shape type: " + nameOfShape);
        }

        return shape;
    }

    /**
     * Extracts the command from the given message.
     *
     * @param message The input message containing the command and other information.
     * @return A substring representing the command (the first word in the message).
     */
    public static String getCommand(String message) {
        return message.substring(0, message.indexOf(" "));
    }

    /**
     * Extracts the ID from the given message.
     *
     * @param message The input message containing an ID at the end.
     * @return An integer representing the ID, which is the last value in the message.
     */
    public static int getId(String message) {
        return Integer.parseInt(message.substring(message.lastIndexOf(" ") + 1));
    }

    /**
     * Extracts the properties of the shape from the given message.
     *
     * @param message The input message containing the shape properties between the command and the ID.
     * @return A substring representing the shape's properties, excluding the command and ID.
     */
    public static String getProperty(String message) {
        return message.substring(message.indexOf(" ") + 1, message.length() - 2);
    }
}