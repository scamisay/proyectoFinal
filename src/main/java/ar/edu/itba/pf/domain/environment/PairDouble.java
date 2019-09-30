package ar.edu.itba.pf.domain.environment;

import java.util.Objects;

public class PairDouble {
    public double x;
    public double y;

    /**
     * constante para inicializar en 0
     */
    public static final PairDouble ZERO = new PairDouble(0, 0);

    public PairDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PairDouble)) return false;
        PairDouble that = (PairDouble) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%5.2f, %5.2f)",x,y);
    }
}
