package ar.edu.itba.pf.domain.environment.objects;

import ar.edu.itba.pf.domain.environment.impl.Cell;

public interface EnvironmentObject {

    int getZ();

    String getStringRepresentarion();

    void setCell(Cell cell);

    double evolve();
}
