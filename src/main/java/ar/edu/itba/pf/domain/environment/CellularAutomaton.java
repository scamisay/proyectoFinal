package ar.edu.itba.pf.domain.environment;

import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;

public interface CellularAutomaton {

    void init(int width, int height);

    void addElement(int x, int y, EnvironmentObject object);

    void evolve();

}
