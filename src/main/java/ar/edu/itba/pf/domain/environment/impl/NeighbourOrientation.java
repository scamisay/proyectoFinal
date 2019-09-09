package ar.edu.itba.pf.domain.environment.impl;

public enum NeighbourOrientation {

    WEST(-1,0),
    NW(-1,1),
    NORTH(0,1),
    NE(1,1),
    EAST(1,0),
    SE(1,-1),
    SOUTH(0,-1),
    SW(-1,-1);

    private int x;
    private int y;

    NeighbourOrientation(int x , int y){
        this.x = x;
        this.y = y;
    }
}
