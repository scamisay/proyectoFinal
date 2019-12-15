package ar.edu.itba.pf.domain.environment.objects;

import ar.edu.itba.pf.domain.environment.impl.Cell;

public class GroundObject implements FieldObject {

    private int z;

    public GroundObject(int z) {
        this.z = z;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public String getStringRepresentarion() {
        return "GROUND";
    }

    @Override
    public void setCell(Cell cell) {
        /*ValidatorHelper.checkNotNull(cell);
        this.cell = cell;*/
    }

    @Override
    public double evolve() {
        return 0;
    }
}
