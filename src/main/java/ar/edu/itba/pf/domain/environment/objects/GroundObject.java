package ar.edu.itba.pf.domain.environment.objects;

import ar.edu.itba.pf.domain.environment.impl.Cell;

public class GroundObject implements FieldObject {

    private double z;

    public GroundObject(double z) {
        this.z = z;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public String getCharacterRepresentarion() {
        return "_";
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
