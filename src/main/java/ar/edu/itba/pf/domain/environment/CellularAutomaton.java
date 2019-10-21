package ar.edu.itba.pf.domain.environment;

import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;
import ar.edu.itba.pf.domain.environment.objects.combustible.CombustibleObject;
import ar.edu.itba.pf.domain.environment.windengine.WindStrategy;

import java.util.List;

public interface CellularAutomaton {

    void init(int width, int height);

    Cell getCell(int x, int y);

    void addElement(int x, int y, EnvironmentObject object);

    void addElement(Pair pair, EnvironmentObject object);

    void addWindStrategy(WindStrategy windStrategy);

    void evolve();

    int getWidth();

    int getHeight();

    int getTime();

    List<Pair> iterate();

    String printObjects();

    String printTemperatures();

    String printWind();

    List<CombustibleObject> getCombustionableObjects();

    boolean isOnFire();
}
