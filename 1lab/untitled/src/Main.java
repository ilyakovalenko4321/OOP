import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Main extends JPanel {
    private List<Shape> shapes;
    private JComboBox<String> shapeSelector;
    private JLabel statusLabel;

    public Main() {
        shapes = new ArrayList<>();

        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.LIGHT_GRAY);

        shapeSelector = new JComboBox<>(new String[]{
                "Отрезок", "Прямоугольник", "Квадрат", "Эллипс", "Окружность", "Треугольник"
        });

        JButton addButton = new JButton("Добавить фигуру");
        JButton clearButton = new JButton("Очистить");
        statusLabel = new JLabel("Фигур: " + shapes.size());

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRandomShape();
                repaint();
                updateStatus();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapes.clear();
                repaint();
                updateStatus();
            }
        });

        controlPanel.add(new JLabel("Выберите фигуру:"));
        controlPanel.add(shapeSelector);
        controlPanel.add(addButton);
        controlPanel.add(clearButton);
        controlPanel.add(statusLabel);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
    }

    private void addRandomShape() {
        int choice = shapeSelector.getSelectedIndex();
        int x = (int)(Math.random() * 650) + 50;
        int y = (int)(Math.random() * 450) + 50;

        switch(choice) {
            case 0:
                shapes.add(new Line(x, y, x + 100, y + 50));
                break;
            case 1:
                shapes.add(new Rectangle(x, y, 80, 60));
                break;
            case 2:
                shapes.add(new Square(x, y, 60));
                break;
            case 3:
                shapes.add(new Ellipse(x, y, 100, 60));
                break;
            case 4:
                shapes.add(new Circle(x, y, 35));
                break;
            case 5:
                shapes.add(new Triangle(x, y, x + 80, y + 80, x - 40, y + 60));
                break;
        }

        // Вывод в консоль
        System.out.println("Добавлена фигура: " + shapes.get(shapes.size() - 1));
    }

    private void updateStatus() {
        statusLabel.setText("Фигур: " + shapes.size());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Shape shape : shapes) {
            if (shape instanceof Line) {
                Line l = (Line) shape;
                g.drawLine(l.x, l.y, l.x + l.width, l.y + l.height);
            } else if (shape instanceof Rectangle) {
                Rectangle r = (Rectangle) shape;
                g.drawRect(r.x, r.y, r.width, r.height);
            } else if (shape instanceof Square) {
                Square s = (Square) shape;
                g.drawRect(s.x, s.y, s.width, s.height);
            } else if (shape instanceof Ellipse) {
                Ellipse e = (Ellipse) shape;
                g.drawOval(e.x, e.y, e.width, e.height);
            } else if (shape instanceof Circle) {
                Circle c = (Circle) shape;
                g.drawOval(c.x, c.y, c.width, c.height);
            } else if (shape instanceof Triangle) {
                Triangle t = (Triangle) shape;
                int[] xPoints = {t.x1, t.x2, t.x3};
                int[] yPoints = {t.y1, t.y2, t.y3};
                g.drawPolygon(xPoints, yPoints, 3);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Геометрические фигуры");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new Main());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}