public abstract class Shape {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public Shape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract String getDescription();

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}