package ar.edu.itba.pf.domain.environment;

import ar.edu.itba.pf.domain.environment.impl.Cell;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;

import java.util.List;

public interface CellularAutomaton {

    void init(int width, int height);

    Cell getCell(int x, int y);

    void addElement(int x, int y, EnvironmentObject object);

    void addElement(Pair pair, EnvironmentObject object);

    void evolve();

    int getWidth();

    int getHeight();

    int getTime();

    List<Pair> iterate();

    String printObjects();

    String printTemperatures();

    List<EnvironmentObject> getCombustionableObjects();
}
