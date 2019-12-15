package ar.edu.itba.pf.domain.environment.impl;

import ar.edu.itba.pf.domain.environment.PairDouble;

import java.util.Arrays;

public enum NeighbourOrientation {
    WEST(-1,0),
    NW(-1,1),
    NORTH(0,1),
    NE(1,1),
    EAST(1,0),
    SE(1,-1),
    SOUTH(0,-1),
    SW(-1,-1);

    /*WEST(-1,0),
    NW(-1,-1),
    NORTH(0,-1),
    NE(1,-1),
    EAST(1,0),
    SE(1,1),
    SOUTH(0,1),
    SW(-1,1);*/

    public int x;
    public int y;

    NeighbourOrientation(int x , int y){
        this.x = x;
        this.y = y;
    }

    public static NeighbourOrientation findByPosition(int xGen, int yGen) {
        return Arrays.stream(values())
                .filter(n -> n.x == xGen && n.y == yGen )
                .findFirst().get();
    }

    public static NeighbourOrientation fromTo(Cell from, Cell to) {
        return findByPosition(to.getX() - from.getX(), to.getY() - from.getY());
    }

    public NeighbourOrientation invert() {
        return findByPosition(x*-1, y*-1);
    }

    public boolean isDiagonal() {
        return (Math.abs(x)+Math.abs(y)) == 2;
    }

    public PairDouble getPair() {
        return new PairDouble(x,y);
    }

}
