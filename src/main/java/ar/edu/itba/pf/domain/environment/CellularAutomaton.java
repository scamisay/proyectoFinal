package ar.edu.itba.pf.domain.environment;

import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;
import ar.edu.itba.pf.domain.environment.windengine.WindStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface CellularAutomaton {

    void setSeed(long seed);

    void init(int width, int height);

    Cell getCell(int x, int y);

    void addElement(int x, int y, EnvironmentObject object);

    void addElement(Pair pair, EnvironmentObject object);

    void addWindStrategy(WindStrategy windStrategy);

    void evolve();

    int getWidth();

    int getHeight();

    long getTime();

    List<Pair> iterate();

    String printObjects();

    String printTemperatures();

    String printCardinals();

    String printAshes();

    String printWind();

    List<CombustibleObject> getCombustionableObjects();

    boolean isOnFire();

    double generateRandomDouble();

    <T> Map<Integer, Map<Integer, T>> buildMatrix(Function<Cell, T> transformation);
}
