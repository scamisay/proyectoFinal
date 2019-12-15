package ar.edu.itba.pf.domain.environment.objects;

import ar.edu.itba.pf.domain.environment.impl.Cell;

public class WaterObject implements FieldObject {

    private int z;

    public WaterObject(int z) {
        this.z = z;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public String getStringRepresentarion() {
        return "WATER";
    }

    @Override
    public void setCell(Cell cell) {

    }

    @Override
    public double evolve() {
        return 0;
    }

}
