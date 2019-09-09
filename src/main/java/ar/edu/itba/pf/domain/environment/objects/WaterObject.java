package ar.edu.itba.pf.domain.environment.objects;

public class WaterObject implements FieldObject {

    private double z;

    public WaterObject(double z) {
        this.z = z;
    }

    @Override
    public double getZ() {
        return z;
    }

}
