package ar.edu.itba.pf.domain.environment.windengine;

/**
 * campo vectorial definido en funcion de posición y tiempo
 */
public interface WindStrategy {

    double getX(int x, int y, int t);

    double getY(int x, int y, int t);
}
