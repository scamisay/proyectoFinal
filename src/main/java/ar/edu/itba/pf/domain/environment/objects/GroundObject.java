package ar.edu.itba.pf.domain.environment.objects;

public class GroundObject implements FieldObject {

    private double z;

    public GroundObject(double z) {
        this.z = z;
    }

    @Override
    public double getZ() {
        return z;
    }
}
