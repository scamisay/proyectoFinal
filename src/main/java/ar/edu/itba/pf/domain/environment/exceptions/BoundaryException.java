package ar.edu.itba.pf.domain.environment.exceptions;

public class BoundaryException extends RuntimeException {

    private int maxX;
    private int maxY;
    private int x;
    private int y;

    public BoundaryException(int width, int height, int x, int y) {
        this.maxX = width - 1;
        this.maxY = height - 1;
        this.x = x;
        this.y = y;
    }

    @Override
    public String getMessage() {
        return String.format("(%d,%d) not in rectangle: [(0,0), (%d,%d)]", x,y, maxX, maxY);
    }
}
