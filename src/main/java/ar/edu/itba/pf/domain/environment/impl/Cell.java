package ar.edu.itba.pf.domain.environment.impl;


import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cell {

    private int x;
    private int y;
    private CellularAutomaton cellularAutomaton;

    public Cell(int x, int y, CellularAutomaton cellularAutomaton) {
        this.x = x;
        this.y = y;
        this.cellularAutomaton = cellularAutomaton;
    }

    /**
     * temperatura generada por los vecinos
     */
    private Map<NeighbourOrientation, Double> temperatures;

    private List<EnvironmentObject> objects = new ArrayList<>();

    public void addObject(EnvironmentObject object) {

    }

    public void evolve() {

    }

    public void updateNeighbourTemperature(double temperature, Cell neighbour){
        int nx = neighbour.getX() - x;
        int ny = neighbour.getY() - y;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
