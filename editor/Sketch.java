import java.util.Map;
import java.util.TreeMap;


/**
 * @author Richmond Nartey Kwalah Tettey CS10, Winter 2025
 * */

public class Sketch {
    private Map<Integer, Shape> sketches; //store all sketches from server
    public static Integer id; //stores id for next shape

    //constructor method
    public Sketch(){
        sketches = new TreeMap();
        id = 0;
    }

    /**
     * method to return map of shapes and id
     * */
    public TreeMap<Integer, Shape> getSketches() {
        return (TreeMap<Integer,Shape>) sketches;
    }

    /**
     * @param shape adds new shape to sketches map
     * */
    public void addNewSketch(Shape shape){
        //add new sketch and increase id by the server
        sketches.put(id++, shape);
    }

    public void addNewSketch(Integer id, Shape shape){
        //add new shape into sketch instance for clients
        sketches.put(id,shape);
    }
}
