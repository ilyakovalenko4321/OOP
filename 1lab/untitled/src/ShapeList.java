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

    public int getCount() {
        return shapes.size();
    }

    public void printAll() {
        System.out.println("=== Список фигур ===");
        System.out.println();
        for (int i = 0; i < shapes.size(); i++) {
            System.out.println((i + 1) + ". " + shapes.get(i));
        }
        System.out.println();
        System.out.println("Всего фигур: " + shapes.size());
    }

    @Override
    public Iterator<Shape> iterator() {
        return shapes.iterator();
    }
}