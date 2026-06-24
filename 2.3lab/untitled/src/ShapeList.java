import shapes.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapeList implements Iterable<Shape> {
    private List<Shape> shapes;

    public ShapeList() {
        shapes = new ArrayList<>();
    }

    public void addShape(Shape shape) {
        if (shape != null) {
            shapes.add(shape);
        }
    }

    public void removeShape(int index) {
        if (index >= 0 && index < shapes.size()) {
            shapes.remove(index);
        }
    }

    public void clear() {
        shapes.clear();
    }

    public Shape getShape(int index) {
        if (index >= 0 && index < shapes.size()) {
            return shapes.get(index);
        }
        return null;
    }

    public int getCount() {
        return shapes.size();
    }

    public List<Shape> getAllShapes() {
        return new ArrayList<>(shapes);
    }

    @Override
    public Iterator<Shape> iterator() {
        return shapes.iterator();
    }

    public void printAll() {
        System.out.println("=== Shape List ===");
        for (int i = 0; i < shapes.size(); i++) {
            System.out.println((i + 1) + ". " + shapes.get(i));
        }
        System.out.println("Total shapes: " + shapes.size());
    }
}