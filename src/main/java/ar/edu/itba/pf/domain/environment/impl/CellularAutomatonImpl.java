package ar.edu.itba.pf.domain.environment.impl;

import ar.edu.itba.pf.domain.drone.Drone;
import ar.edu.itba.pf.domain.environment.CellularAutomaton;
import ar.edu.itba.pf.domain.environment.exceptions.BoundaryException;
import ar.edu.itba.pf.domain.environment.objects.EnvironmentObject;

import java.util.ArrayList;
import java.util.List;

public class CellularAutomatonImpl implements CellularAutomaton {

    private int width;
    private int height;
    private int t;
    private Cell[][] cells;
    private List<Drone> drones = new ArrayList<>();

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        t = 0;
        cells = new Cell[width][height];
    }

    @Override
    public void addElement(int x, int y, EnvironmentObject object) {
        checkBoundaries(x,y);
        cells[x][y].addObject(object);
    }

    private void checkBoundaries(int x, int y){
        if(x < 0 || x >= width || y < 0 || y >= height){
            throw new BoundaryException(width, height, x, y);
        }
    }


    @Override
    public void evolve() {
        t++;
        for(int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                cells[x][y].evolve();
            }
        }
    }
}
